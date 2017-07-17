package pl.confitura.jelatyna.registration;

import static java.time.LocalDateTime.now;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Streams;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.opencsv.CSVReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.mail.MailSender;
import pl.confitura.jelatyna.mail.MessageInfo;

@RepositoryRestController
@Slf4j
@AllArgsConstructor
public class RegistrationController {

    private MailSender sender;
    private ParticipantRepository repository;
    private TicketGenerator generator;

    @PostMapping("/participants/upload")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> upload(@RequestParam MultipartFile file, @AuthenticationPrincipal JelatynaPrincipal principal)
            throws IOException {
        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()), ';');
        reader.forEach(row -> register(row[0], Integer.parseInt(row[1]), principal));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/reminder")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> reminder()
            throws IOException {
        doSendRemindTo(repository.findAllUnregistered());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/ticket")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> sendTickets() throws IOException {
        doSendTicketTo(repository.findAllRegistered());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/survey")
    @PreAuthorize("@security.isAdmin()")
    @Transactional
    public ResponseEntity<Object> sendSurveys() throws IOException {
        doSendSurveyTo(repository.findAllRegistered());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/participants/{id}")
    @Transactional
    public ResponseEntity<Object> save(@RequestBody Participant participant, @PathVariable String id) {
        repository.findOne(id)
                .setName(participant.getName())
                .setCity(participant.getCity())
                .setEmail(participant.getEmail())
                .setExperience(participant.getExperience())
                .setGender(participant.getGender())
                .setRole(participant.getRole())
                .setSize(participant.getSize())
                .setRegistrationDate(LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/participants/{id}/arrived")
    @PreAuthorize("@security.isVolunteer()")
    @Transactional
    public ResponseEntity<Object> arrived(@PathVariable String id, @AuthenticationPrincipal Authentication authentication) {
        Participant participant = repository.findOne(id);
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

    private void register(String mail, int count, JelatynaPrincipal principal) {
        String creatorName = principal.getName();
        IntStream.range(0, count).forEach((it) -> {
            try {
                Participant participant =
                        repository.save(new Participant().setCreationDate(now()).setOriginalBuyer(mail).setCreatedBy(creatorName));
                sender.send("pre-registration", new MessageInfo().setEmail(mail).setToken(participant.getId()));
                participant.setEmailSent(true);
            } catch (Exception ex) {
                log.error("Error on sending email", ex);
            }

        });

    }

    @Async
    private void doSendRemindTo(Iterable<Participant> participants) {
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
    private void doSendTicketTo(Iterable<Participant> participants) {
        Streams.stream(participants)
                .filter(Participant::ticketNotSentYet)
                .forEach(this::sendTicketTo);
    }

    private void sendTicketTo(Participant participant) {
        try {
            doSendTicketTo(participant);
        } catch (Exception e) {
            log.error("Error on sending ticket to {}", participant.getOriginalBuyer());
            log.error("Exception from sender:", e);
        }
    }

    @Transactional
    private void doSendTicketTo(Participant participant) throws IOException, MandrillApiError {
        MessageInfo info = new MessageInfo()
                .setEmail(participant.getEmail())
                .setName(participant.getName())
                .setTicket(generator.generateFor(participant.getId()));
        sender.send("registration-ticket", info);
        repository.save(participant.setTicketSendDate(LocalDateTime.now()));
    }

    @Async
    private void doSendSurveyTo(Iterable<Participant> participants) {
        Streams.stream(participants)
                .filter(Participant::alreadyArrived)
                .filter(Participant::surveyNotSentYet)
                .forEach(this::sendSurveyTo);
    }


    private void sendSurveyTo(Participant participant) {
        try {
            doSendSurvey(participant);
        } catch (Exception e) {
            log.error("Error on sending survey to {}", participant.getOriginalBuyer());
            log.error("Exception from sender:", e);
        }
    }

    @Transactional
    private void doSendSurvey(Participant participant) throws IOException, MandrillApiError {
        MessageInfo info = new MessageInfo()
                .setEmail(participant.getEmail())
                .setName(participant.getName());
        sender.send("survey", info);
        repository.save(participant.setSurveySendDate(LocalDateTime.now()));
    }
}
