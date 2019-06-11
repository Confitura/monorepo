package pl.confitura.jelatyna.registration;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletResponse;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil;
import pl.confitura.jelatyna.mail.MailSender;
import pl.confitura.jelatyna.mail.MessageInfo;
import pl.confitura.jelatyna.registration.demographic.DemographicDataRepository;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;

@RepositoryRestController
@Slf4j
@AllArgsConstructor
public class RegistrationController {

    private MailSender sender;
    private ParticipationRepository repository;
    private VoucherService voucherService;
    private TicketGenerator generator;
    private DemographicDataRepository demographicDataRepository;

    @GetMapping("/participants")
    @PreAuthorize("@security.isAdmin()")
    ResponseEntity<Iterable<Participant>> getParticipants(@AuthenticationPrincipal Authentication authentication) {
        List<Participant> list = repository.findAll().stream()
                .map(it -> new Participant(it, (JelatynaPrincipal) authentication.getPrincipal()))
                .collect(toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping("/participants/reminder")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> reminder() {
        doSendRemindTo(voucherService.findUnusedVouchers());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/ticket")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> sendTickets() {
        doSendTicketTo(repository.findUsersToSendTickets());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/survey")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> sendSurveys() {
        doSendSurveyTo(repository.findAllPresentOnConference());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants")
    @Transactional
    public ResponseEntity<Object> save(@RequestBody RegistrationForm registrationForm) {
        JelatynaPrincipal principal = SecurityContextUtil.getPrincipal();
        Voucher voucher = registrationForm.getVoucher();
        if (voucher != null) {
            if (!voucherService.isValid(voucher)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_VOUCHER");
            } else if (voucherService.isUsed(voucher)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("VOUCHER_USED");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("MISSING_VOUCHER");
        }
        ParticipationData participation = saveParticipation(registrationForm);
        sendTicketTo(participation);
        saveStatistics(registrationForm);
        return ResponseEntity.ok(participation);
    }

    private void saveStatistics(RegistrationForm registrationForm) {
        demographicDataRepository.save(registrationForm.createDemographicData());
    }

    private ParticipationData saveParticipation(RegistrationForm registrationForm) {
        return repository.save(registrationForm.createParticipant());
    }

    @GetMapping("/participants/{id}")
    @Transactional
    public ResponseEntity<Object> findOne(@PathVariable String id) {
        ParticipationData data = repository.findById(id);
        if (data == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(new ParticipationDataPublic(data));
        }
    }

    @GetMapping("/participants/{id}/ticket")
    @Transactional
    public void getQrCode(@PathVariable String id, HttpServletResponse response) throws IOException {
        byte[] ticket = generator.generateFor(id);
        response.setContentType(MediaType.IMAGE_PNG.toString());
        response.getOutputStream().write(ticket);
    }

    @PutMapping("/participants/{id}")
    @Transactional
    public ResponseEntity<Object> save(@RequestBody ParticipationData participationData, @PathVariable String id) {
        if (participationData.getVoucher() != null) {
            if (!voucherService.isValid(participationData.getVoucher())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_VOUCHER");
            }
        }
        if (voucherService.canAssign(id, participationData.getVoucher())) {
            repository.findById(id)
                    .setVoucher(participationData.getVoucher());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/participants/{id}/arrived")
    @PreAuthorize("@security.isVolunteer()")
    @Transactional
    public ResponseEntity<Object> arrived(
            @PathVariable String id,
            @AuthenticationPrincipal Authentication authentication) {
        ParticipationData user = repository.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        } else {
            return arrived(user, (JelatynaPrincipal) authentication.getPrincipal());
        }
    }

    private ResponseEntity<Object> arrived(ParticipationData participationData, JelatynaPrincipal registerer) {
        HttpStatus status = HttpStatus.OK;
        if (participationData.alreadyArrived()) {
            status = HttpStatus.CONFLICT;
        } else {
            participationData
                    .setArrivalDate(LocalDateTime.now())
                    .setRegisteredBy(registerer.getId());
        }
        Participant participant = new Participant(participationData, registerer);
        return ResponseEntity.status(status).body(participant);
    }

    @Async
    void doSendRemindTo(Iterable<Voucher> vouchers) {
        vouchers
                .forEach(voucher -> {
                    try {
                        MessageInfo info = new MessageInfo()
                                .setEmail(voucher.getOriginalBuyer())
                                .setToken(voucher.getId());
                        sender.send("registration-reminder", info);
                    } catch (Exception e) {
                        log.error("Error on sending reminder user to {}", voucher.getOriginalBuyer());
                    }
                });
    }

    @Async
    void doSendTicketTo(Iterable<ParticipationData> users) {
        users.forEach(this::sendTicketTo);
    }

    private void sendTicketTo(ParticipationData user) {
        try {
            doSendTicketTo(user);
        } catch (Exception e) {
            log.error("Error on sending ticket to {}", user.getEmail());
            log.error("Exception from sender:", e);
        }
    }

    @Transactional
    void doSendTicketTo(ParticipationData user) throws IOException, MandrillApiError {
        MessageInfo info = new MessageInfo()
                .setToken(user.getId())
                .setEmail(user.getEmail())
                .setName(user.getFullName())
                .setTicket(generator.generateFor(user.getId()));
        sender.send("registration-ticket", info);
        repository.save(user.setTicketSendDate(LocalDateTime.now()));
    }

    @Async
    void doSendSurveyTo(Iterable<ParticipationData> users) {
        StreamSupport.stream(users.spliterator(), false)
                .filter(ParticipationData::alreadyArrived)
                .filter(ParticipationData::surveyNotSentYet)
                .forEach(this::sendSurveyTo);
    }

    private void sendSurveyTo(ParticipationData user) {
        try {
            doSendSurvey(user);
        } catch (Exception e) {
            log.error("Error on sending survey to {}", user.getEmail());
            log.error("Exception from sender:", e);
        }
    }

    @Transactional
    void doSendSurvey(ParticipationData user) throws IOException, MandrillApiError {
        MessageInfo info = new MessageInfo()
                .setEmail(user.getEmail())
                .setName(user.getFullName());
        sender.send("survey", info);
        repository.save(user.setSurveySendDate(LocalDateTime.now()));
    }
}
