package pl.confitura.jelatyna.infrastructure.admintasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.infrastructure.archive.WebpageDataDumper;

import java.time.Instant;

@RestController
@RequestMapping("admin-tasks")
@PreAuthorize("@security.isAdmin()")
@RequiredArgsConstructor
@Slf4j
public class AdminTaskController {

    private final WebpageDataDumper webpageDataDumper;

    @PostMapping("/webpage/dump")
    public DumpResponse triggerWebpageDump() {
        log.info("Admin requested manual webpage data dump");
        webpageDataDumper.dumpAll();
        return new DumpResponse(Instant.now().toString());
    }

    @GetMapping("/webpage/last-dump")
    public DumpStatus getLastWebpageDump() {
        Instant last = webpageDataDumper.getLastDumpAt();
        return new DumpStatus(last == null ? null : last.toString());
    }

    public record DumpResponse(String triggeredAt) {}
    public record DumpStatus(String lastDumpAt) {}
}
