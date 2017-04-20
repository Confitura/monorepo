package pl.confitura.jelatyna.resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import pl.confitura.jelatyna.infrastructure.security.JelatynaPrincipal;
import pl.confitura.jelatyna.partner.Partner;
import pl.confitura.jelatyna.partner.PartnerRepository;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

@Service
public class ResourceStorage {
    @Value("${resources.path}")
    private String rootPath;
    @Value("${resources.folder}")
    private String folder;
    @Value("${server.context-path}")
    private String contextPath;

    private UserRepository repository;
    private PartnerRepository partnerRepository;

    @Autowired
    public ResourceStorage(UserRepository repository, PartnerRepository partnerRepository) {
        this.repository = repository;
        this.partnerRepository = partnerRepository;
    }

    @Transactional
    void storeSpeaker(@RequestParam MultipartFile file, JelatynaPrincipal principal) throws IOException {
        User user = repository.findOne(principal.getId());
        String path = doStore(user.getId(), file, "photos");
        user.setPhoto(path);
        repository.save(user);
    }

    @Transactional
    @PreAuthorize("@security.isAdmin()")
    void storePartnerLogo(@RequestParam MultipartFile file, String id) throws IOException {
        Partner partner = partnerRepository.findOne(id);
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
