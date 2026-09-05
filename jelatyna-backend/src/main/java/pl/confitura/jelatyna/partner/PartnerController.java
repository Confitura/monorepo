package pl.confitura.jelatyna.partner;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Manages sponsors/partners. Public reads expose only published partners; writes are
 * admin-only (guarded on the repository's save/deleteById). The logo is uploaded separately
 * via POST /resources/partners/{id}.
 */
@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerRepository repository;

    /** Public: published partners only. */
    @GetMapping
    public List<PartnerDto> getPublishedPartners() {
        return repository.findPublished().stream().map(PartnerDto::from).toList();
    }

    /** Admin: all partners including unpublished, for the management screen. */
    @GetMapping("/all")
    @PreAuthorize("@security.isAdmin()")
    public List<PartnerDto> getAllPartners() {
        return repository.findAll().stream().map(PartnerDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartnerDto> getPartner(@PathVariable String id) {
        Partner partner = repository.findById(id);
        return partner == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(PartnerDto.from(partner));
    }

    @PostMapping
    public ResponseEntity<PartnerDto> createPartner(@RequestBody PartnerRequest request) {
        Partner partner = new Partner();
        partner.setName(request.name());
        partner.setType(request.type());
        partner.setWww(request.www());
        partner.setDescription(request.description());
        partner.setOrientation(request.orientation());
        partner.setPublished(Boolean.TRUE.equals(request.published()));
        return ResponseEntity.status(HttpStatus.CREATED).body(PartnerDto.from(repository.save(partner)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartnerDto> updatePartner(@PathVariable String id, @RequestBody PartnerRequest request) {
        Partner partner = repository.findById(id);
        if (partner == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.name() != null) partner.setName(request.name());
        if (request.type() != null) partner.setType(request.type());
        if (request.www() != null) partner.setWww(request.www());
        if (request.description() != null) partner.setDescription(request.description());
        if (request.orientation() != null) partner.setOrientation(request.orientation());
        if (request.published() != null) partner.setPublished(request.published());
        return ResponseEntity.ok(PartnerDto.from(repository.save(partner)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
