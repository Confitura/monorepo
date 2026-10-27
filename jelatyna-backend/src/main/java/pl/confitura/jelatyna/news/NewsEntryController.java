package pl.confitura.jelatyna.news;

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

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Lets admins manage News Entries. Every endpoint is admin-only, as unpublished and scheduled
 * entries must not leak; the public reads its entries from the webpage dump.
 */
@RestController
@RequestMapping("/news-entries")
@PreAuthorize("@security.isAdmin()")
@RequiredArgsConstructor
public class NewsEntryController {

    // What the slug rule can make: runs of lowercase ASCII letters and digits joined by single hyphens.
    private static final Pattern SLUG = Pattern.compile("[a-z0-9]+(-[a-z0-9]+)*");

    private final NewsEntryRepository repository;

    /** Every entry, unpublished and scheduled ones included, newest date first. */
    @GetMapping
    public List<NewsEntryDto> getAllNewsEntries() {
        return repository.findAllByOrderByDateDesc().stream().map(NewsEntryDto::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsEntryDto> getNewsEntry(@PathVariable String id) {
        NewsEntry entry = repository.findById(id);
        return entry == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(NewsEntryDto.from(entry));
    }

    /**
     * Without a slug, takes the title's by the slug rule, numbered -2, -3, … past those in use. A
     * given slug is kept as it is, so one another entry has is refused rather than renumbered.
     */
    @PostMapping
    public ResponseEntity<NewsEntryDto> createNewsEntry(@RequestBody NewsEntryRequest request) {
        if (request.title() == null || request.body() == null || request.date() == null) {
            return ResponseEntity.badRequest().build();
        }
        String slug = request.slug() == null ? freeSlug(slugOf(request.title())) : request.slug();
        HttpStatus refusal = refusalOf(slug);
        if (refusal != null) {
            return ResponseEntity.status(refusal).build();
        }
        NewsEntry entry = new NewsEntry();
        entry.setTitle(request.title());
        entry.setSlug(slug);
        entry.setBody(request.body());
        entry.setDate(request.date());
        entry.setPublished(Boolean.TRUE.equals(request.published()));
        return ResponseEntity.status(HttpStatus.CREATED).body(NewsEntryDto.from(repository.save(entry)));
    }

    /**
     * Changes only the fields given. A new title keeps the slug, so shared links keep working; a new
     * slug is refused like a given one on create, but the entry's own current slug never clashes. The
     * cover is set only by the upload, POST /resources/news-entries/{id}, and removed by removeCover.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NewsEntryDto> updateNewsEntry(@PathVariable String id, @RequestBody NewsEntryRequest request) {
        NewsEntry entry = repository.findById(id);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.slug() != null && !request.slug().equals(entry.getSlug())) {
            HttpStatus refusal = refusalOf(request.slug());
            if (refusal != null) {
                return ResponseEntity.status(refusal).build();
            }
            entry.setSlug(request.slug());
        }
        if (request.title() != null) entry.setTitle(request.title());
        if (request.body() != null) entry.setBody(request.body());
        if (request.date() != null) entry.setDate(request.date());
        if (request.published() != null) entry.setPublished(request.published());
        if (Boolean.TRUE.equals(request.removeCover())) entry.setCover(null);
        return ResponseEntity.ok(NewsEntryDto.from(repository.save(entry)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNewsEntry(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Why no entry may take the slug (not in the slug rule's form, or in use), or null if one may. */
    private HttpStatus refusalOf(String slug) {
        if (!SLUG.matcher(slug).matches()) {
            return HttpStatus.BAD_REQUEST;
        }
        return repository.existsBySlug(slug) ? HttpStatus.CONFLICT : null;
    }

    /** The slug itself if no entry has it, otherwise the first of slug-2, slug-3, … that none has. */
    private String freeSlug(String slug) {
        String free = slug;
        for (int n = 2; repository.existsBySlug(free); n++) {
            free = slug + "-" + n;
        }
        return free;
    }

    /**
     * The slug rule, the one the seed migration's slugs follow: lowercase, diacritics folded to ASCII,
     * apostrophes (' and ’) dropped, every other run of non-alphanumerics one hyphen, no hyphen at
     * either end. Decomposing drops accents but leaves ł whole, so it is folded by hand.
     */
    private static String slugOf(String title) {
        String lowercase = title.toLowerCase(Locale.ROOT).replace('ł', 'l');
        return Normalizer.normalize(lowercase, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("['’]", "")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
