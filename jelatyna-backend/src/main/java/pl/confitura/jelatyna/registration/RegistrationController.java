package pl.confitura.jelatyna.registration;

import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.SecurityContextUtil;
import pl.confitura.jelatyna.mail.MailSender;
import pl.confitura.jelatyna.mail.MessageInfo;
import pl.confitura.jelatyna.registration.demographic.DemographicDataRepository;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@RepositoryRestController
@Slf4j
@AllArgsConstructor
public class RegistrationController {

    private MailSender sender;
    private UserRepository userRepository;
    private ParticipationRepository repository;
    private VoucherService voucherService;
    private TicketGenerator generator;
    private DemographicDataRepository demographicDataRepository;

    @GetMapping("/participants")
    ResponseEntity<Iterable<Participant>> getParticipants() {
        List<Participant> list = userRepository.findParticipants().stream()
                .map(Participant::new)
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
        doSendTicketTo(userRepository.findUsersToSendTickets());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/survey")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> sendSurveys() {
        doSendSurveyTo(userRepository.findAllPresentOnConference());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> save(@RequestBody RegistrationForm registrationForm) {
        JelatynaPrincipal principal = SecurityContextUtil.getPrincipal();
        User user = userRepository.findById(principal.id);
        if (user.getParticipationData() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (registrationForm.getVoucher() != null) {
            if (!voucherService.isValid(registrationForm.getVoucher())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INVALID_VOUCHER");
            }
        }
        saveParticipation(registrationForm, user);
        saveDemographic(registrationForm);
        return ResponseEntity.ok().build();
    }

    private void saveDemographic(RegistrationForm registrationForm) {
        demographicDataRepository.save(registrationForm.createDemographicData());
    }

    private void saveParticipation(RegistrationForm registrationForm, User user) {
        ParticipationData saved = repository.save(registrationForm.createParticipant());
        user.setParticipationData(saved);
        userRepository.save(user);
    }


    @PutMapping("/participants/{id}")
    @Transactional
    @PreAuthorize("@security.isUserAnOwnerOfParticipationData(#id)")
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
    public ResponseEntity<Object> arrived(@PathVariable String id, @AuthenticationPrincipal Authentication authentication) {
        ParticipationData participationData = repository.findById(id);
        if (participationData == null) {
            return ResponseEntity.notFound().build();
        } else {
            return arrived(participationData, (JelatynaPrincipal) authentication.getPrincipal());
        }
    }

    private ResponseEntity<Object> arrived(ParticipationData participationData, JelatynaPrincipal principal) {
        HttpStatus status = HttpStatus.OK;
        if (participationData.alreadyArrived()) {
            status = HttpStatus.CONFLICT;
        } else {
            participationData
                    .setArrivalDate(LocalDateTime.now())
                    .setRegisteredBy(principal.getId());
        }
        return ResponseEntity.status(status).body(participationData);
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
    void doSendTicketTo(Iterable<User> users) {
        users.forEach(this::sendTicketTo);
    }

    private void sendTicketTo(User user) {
        try {
            doSendTicketTo(user);
        } catch (Exception e) {
            log.error("Error on sending ticket to {}", user.getEmail());
            log.error("Exception from sender:", e);
        }
    }

    @Transactional
    void doSendTicketTo(User user) throws IOException, MandrillApiError {
        MessageInfo info = new MessageInfo()
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setTicket(generator.generateFor(user.getId()));
        sender.send("registration-ticket", info);
        repository.save(user.getParticipationData().setTicketSendDate(LocalDateTime.now()));
    }

    @Async
    void doSendSurveyTo(Iterable<User> users) {
        StreamSupport.stream(users.spliterator(), false)
                .filter(it -> it.getParticipationData().alreadyArrived())
                .filter(it -> it.getParticipationData().surveyNotSentYet())
                .forEach(this::sendSurveyTo);
    }


    private void sendSurveyTo(User user) {
        try {
            doSendSurvey(user);
        } catch (Exception e) {
            log.error("Error on sending survey to {}", user.getEmail());
            log.error("Exception from sender:", e);
        }
    }

    @Transactional
    void doSendSurvey(User user) throws IOException, MandrillApiError {
        MessageInfo info = new MessageInfo()
                .setEmail(user.getEmail())
                .setName(user.getName());
        sender.send("survey", info);
        repository.save(user.getParticipationData().setSurveySendDate(LocalDateTime.now()));
    }
}
