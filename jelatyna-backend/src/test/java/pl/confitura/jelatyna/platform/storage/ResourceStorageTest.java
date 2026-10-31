package pl.confitura.jelatyna.platform.storage;

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

class ResourceStorageTest {

    @TempDir
    Path folder;

    private ResourceStorage storage() {
        ResourceConfigurationProperties properties =
                new ResourceConfigurationProperties("resources", folder.toString(), "https://res.example");
        return new ResourceStorage(properties);
    }

    private MultipartFile file(String originalFilename) throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(originalFilename);
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        return file;
    }

    @Test
    void storesAllowedImageWithinStorageDirectory() throws IOException {
        String url = storage().store("p1", file("logo.PNG"), "photos");

        assertThat(Files.exists(folder.resolve("photos").resolve("p1.png"))).isTrue();
        assertThat(url).isEqualTo("https://res.example/resources/photos/p1.png");
    }

    @Test
    void rejectsPathTraversalInFileName() throws IOException {
        assertThatThrownBy(() -> storage().store("../../evil", file("logo.png"), "photos"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(Files.exists(folder.getParent().getParent().resolve("evil.png"))).isFalse();
    }

    @Test
    void rejectsDisallowedExtension() throws IOException {
        assertThatThrownBy(() -> storage().store("p1", file("evil.php"), "photos"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
