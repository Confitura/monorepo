package pl.confitura.jelatyna.registration;

import static java.time.LocalDateTime.now;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.mail.MailSender;

//@RestController
@RepositoryRestController
@Slf4j
public class RegistrationController {

    private MailSender sender;
    private ParticipantRepository repository;

    @Autowired
    public RegistrationController(MailSender sender, ParticipantRepository repository) {
        this.sender = sender;
        this.repository = repository;
    }

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

    @Async
    private void doSendRemindTo(Iterable<Participant> participants) {
        Streams.stream(participants)
                .forEach(participant -> {
                    try {
                        sender.send(participant.getOriginalBuyer(), "registration-reminder", new HashMap<>());
                    } catch (Exception e) {
                        log.error("Error on sending reminder user to {}", participant.getOriginalBuyer());
                    }
                });
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

    private void register(String mail, int count, JelatynaPrincipal principal) {
        String creatorName = principal.getName();
        IntStream.range(0, count).forEach((it) -> {
            try {
                Participant participant =
                        repository.save(new Participant().setCreationDate(now()).setOriginalBuyer(mail).setCreatedBy(creatorName));
                sender.send(mail, "pre-registration", ImmutableMap.of("token", participant.getId()));
                participant.setEmailSent(true);
            } catch (Exception ex) {
                log.error("Error on sending email", ex);
            }

        });
    }
}
