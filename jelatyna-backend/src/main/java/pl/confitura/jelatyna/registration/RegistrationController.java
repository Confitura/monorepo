package pl.confitura.jelatyna.registration;

import com.google.common.collect.Streams;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.infrastructure.security.Security;
import pl.confitura.jelatyna.mail.MailSender;
import pl.confitura.jelatyna.mail.MessageInfo;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;

@RepositoryRestController
@Slf4j
@AllArgsConstructor
public class RegistrationController {

    private MailSender sender;
    private UserRepository userRepository;
    private ParticipantRepository repository;
    private VoucherService voucherService;
    private TicketGenerator generator;

    @PostMapping("/participants/reminder")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> reminder()
            throws IOException {
        doSendRemindTo(voucherService.findUnusedVouchers());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/ticket")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> sendTickets() throws IOException {
        doSendTicketTo(userRepository.findUsersToSendTickets());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/survey")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> sendSurveys() throws IOException {
        doSendSurveyTo(userRepository.findAllRegistered());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants")
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> save(@RequestBody Participant participant) {
        JelatynaPrincipal principal = Security.getPrincipal();
        User user = userRepository.findById(principal.id);
        if (user.getParticipant() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (participant.getVoucher() != null) {
            if (!voucherService.isValid(participant.getVoucher())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        Participant saved = repository.save(participant.setId(null));
        user.setParticipant(saved);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/participants/{id}")
    @Transactional
    @PreAuthorize("@security.userRegisteredAsParticipant(#id)")
    public ResponseEntity<Object> save(@RequestBody Participant participant, @PathVariable String id) {
        if (voucherService.canAssign(id, participant.getVoucher())) {
            repository.findById(id)
                    .setVoucher(participant.getVoucher());
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/participants/{id}/arrived")
    @PreAuthorize("@security.isVolunteer()")
    @Transactional
    public ResponseEntity<Object> arrived(@PathVariable String id, @AuthenticationPrincipal Authentication authentication) {
        Participant participant = repository.findById(id);
        if (participant == null) {
            return ResponseEntity.notFound().build();
        } else {
            return arrived(participant, (JelatynaPrincipal) authentication.getPrincipal());
        }
    }

    private ResponseEntity<Object> arrived(Participant participant, JelatynaPrincipal principal) {
        HttpStatus status = HttpStatus.OK;
        if (participant.alreadyArrived()) {
            status = HttpStatus.CONFLICT;
        } else {
            participant
                    .setArrivalDate(LocalDateTime.now())
                    .setRegisteredBy(principal.getId());
        }
        return ResponseEntity.status(status).body(participant);
    }


    @Async
    void doSendRemindTo(Iterable<Voucher> participants) {
        Streams.stream(participants)
                .forEach(participant -> {
                    try {
                        MessageInfo info = new MessageInfo()
                                .setEmail(participant.getOriginalBuyer())
                                .setToken(participant.getId());
                        sender.send("registration-reminder", info);
                    } catch (Exception e) {
                        log.error("Error on sending reminder user to {}", participant.getOriginalBuyer());
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
        repository.save(user.getParticipant().setTicketSendDate(LocalDateTime.now()));
    }

    @Async
    void doSendSurveyTo(Iterable<User> users) {
        Streams.stream(users)
                .filter(it -> it.getParticipant().alreadyArrived())
                .filter(it -> it.getParticipant().surveyNotSentYet())
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
        repository.save(user.getParticipant().setSurveySendDate(LocalDateTime.now()));
    }
}
