package pl.confitura.jelatyna.webpage.partner.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pl.confitura.jelatyna.platform.storage.ResourceStorage;
import pl.confitura.jelatyna.webpage.partner.domain.Partner;
import pl.confitura.jelatyna.webpage.partner.domain.PartnerRepository;

import java.io.IOException;
import java.util.List;

/**
 * Manages sponsors/partners. Public reads expose only published partners; writes are
 * admin-only (guarded on the repository's save/deleteById). The logo is uploaded separately
 * via POST /resources/partners/{id}.
 */
@RestController
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerRepository repository;
    private final ResourceStorage storage;

    @GetMapping("/partners")
    public List<PartnerDto> getPublishedPartners() {
        return repository.findPublished().stream().map(PartnerDto::from).toList();
    }

    @GetMapping("/partners/all")
    @PreAuthorize("@security.isAdmin()")
    public List<PartnerDto> getAllPartners() {
        return repository.findAll().stream().map(PartnerDto::from).toList();
    }

    @GetMapping("/partners/{id}")
    public ResponseEntity<PartnerDto> getPartner(@PathVariable String id) {
        Partner partner = repository.findById(id);
        return partner == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(PartnerDto.from(partner));
    }

    @PostMapping("/partners")
    public ResponseEntity<PartnerDto> createPartner(@RequestBody PartnerRequest request) {
        Partner partner = new Partner();
        partner.setSlug(request.slug());
        partner.setName(request.name());
        partner.setType(request.type());
        partner.setWww(request.www());
        partner.setDescription(request.description());
        partner.setOrientation(request.orientation());
        partner.setPublished(Boolean.TRUE.equals(request.published()));
        return ResponseEntity.status(HttpStatus.CREATED).body(PartnerDto.from(repository.save(partner)));
    }

    @PutMapping("/partners/{id}")
    public ResponseEntity<PartnerDto> updatePartner(@PathVariable String id, @RequestBody PartnerRequest request) {
        Partner partner = repository.findById(id);
        if (partner == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.slug() != null) partner.setSlug(request.slug());
        if (request.name() != null) partner.setName(request.name());
        if (request.type() != null) partner.setType(request.type());
        if (request.www() != null) partner.setWww(request.www());
        if (request.description() != null) partner.setDescription(request.description());
        if (request.orientation() != null) partner.setOrientation(request.orientation());
        if (request.published() != null) partner.setPublished(request.published());
        return ResponseEntity.ok(PartnerDto.from(repository.save(partner)));
    }

    @DeleteMapping("/partners/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/resources/partners/{id}")
    @Operation(
            summary = "Store partner logo",
            description = "Upload a Multipart file for the partner's logo",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "File to upload",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schemaProperties = @SchemaProperty(
                                    name = "file",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    )
            ),
            parameters = @Parameter(name = "id", description = "ID of the partner", required = true)
    )
    @Transactional
    @PreAuthorize("@security.isAdmin()")
    public void storePartnerLogo(@RequestParam("file") MultipartFile file, @PathVariable String id) throws IOException {
        Partner partner = repository.findById(id);
        partner.setLogo(storage.store(id, file, "photos"));
        repository.save(partner);
    }
}
