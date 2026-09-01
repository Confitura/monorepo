package pl.confitura.jelatyna.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.multipart.MultipartFile;

import pl.confitura.jelatyna.partner.Partner;
import pl.confitura.jelatyna.partner.PartnerRepository;
import pl.confitura.jelatyna.user.UserRepository;

class ResourceStorageTest {

    @TempDir
    Path folder;

    private final PartnerRepository partnerRepository = mock(PartnerRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    private ResourceStorage storage() {
        ResourceConfigurationProperties properties =
                new ResourceConfigurationProperties("resources", folder.toString(), "https://res.example");
        return new ResourceStorage(properties, userRepository, partnerRepository);
    }

    private MultipartFile file(String originalFilename) throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(originalFilename);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        return file;
    }

    @Test
    void storesAllowedImageWithinStorageDirectory() throws IOException {
        when(partnerRepository.findById("p1")).thenReturn(mock(Partner.class));

        storage().storePartnerLogo(file("logo.PNG"), "p1");

        assertThat(Files.exists(folder.resolve("photos").resolve("p1.png"))).isTrue();
    }

    @Test
    void rejectsPathTraversalInFileName() throws IOException {
        when(partnerRepository.findById("../../evil")).thenReturn(mock(Partner.class));

        assertThatThrownBy(() -> storage().storePartnerLogo(file("logo.png"), "../../evil"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(Files.exists(folder.getParent().getParent().resolve("evil.png"))).isFalse();
    }

    @Test
    void rejectsDisallowedExtension() throws IOException {
        when(partnerRepository.findById("p1")).thenReturn(mock(Partner.class));

        assertThatThrownBy(() -> storage().storePartnerLogo(file("evil.php"), "p1"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
