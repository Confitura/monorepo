package pl.confitura.jelatyna.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.confitura.jelatyna.partner.Partner;
import pl.confitura.jelatyna.partner.PartnerRepository;
import pl.confitura.jelatyna.user.UserFacade;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
public class ResourceStorage {

    @Value("${resources.path}")
    private String rootPath;
    @Value("${resources.folder}")
    private String folder;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final UserFacade userFacade;
    private final PartnerRepository partnerRepository;

    @Transactional
    @PreAuthorize("@security.isOwner(#userId)")
    void storeSpeaker(@RequestParam MultipartFile file, String userId) throws IOException {
        String path = doStore(userId, file, "photos");
        userFacade.changePhoto(userId, path);
    }

    @Transactional
    @PreAuthorize("@security.isAdmin()")
    void storePartnerLogo(@RequestParam MultipartFile file, String id) throws IOException {
        Partner partner = partnerRepository.findById(id);
        partner.setLogo(doStore(id, file, "photos"));
        partnerRepository.save(partner);
    }

    private String doStore(String fileName, MultipartFile file, String... paths) throws IOException {
        Path path = Files.createDirectories(Paths.get(folder, paths));
        Path filePath =
                Paths.get(path.toString(), fileName + "." + com.google.common.io.Files.getFileExtension(file.getOriginalFilename()));
        Files.write(filePath, file.getBytes());
        return contextPath + "/" + rootPath + "/" + filePath.toString().replace(folder, "");
    }
}
