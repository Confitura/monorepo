package pl.confitura.jelatyna.conference.presentation.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.conference.edition.domain.Edition;
import pl.confitura.jelatyna.identity.user.domain.User;

@Entity
@Data
@ToString(exclude = "speakers")
@EqualsAndHashCode(exclude = "speakers")
@Accessors(chain = true)
public class Presentation {

    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REPORTED = "reported";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "varchar(100)")
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    @Column(columnDefinition = "text")
    private String shortDescription;
    @NotBlank()
    @Column(columnDefinition = "text")
    private String description;
    @NotBlank
    private String level;
    @NotBlank
    private String language;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    // The column is named after User's old inverse side (presentations), which no longer exists.
    @ManyToMany
    @JoinTable(name = "presentation_speakers", joinColumns = @JoinColumn(name = "presentations_id"))
    @NotNull
    private Set<User> speakers = new HashSet<>();

    // The Edition that owns this talk (ADR-0004): the Current Edition when it was created.
    @ManyToOne
    @NotNull
    @JsonIgnore
    private Edition edition;

    private String status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255)")
    private PreSelectionStatus preSelectionStatus = PreSelectionStatus.NONE;

    private String preSelectionComment;

    private boolean workshop = false;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean ratingEnabled = true;

    private Boolean isFree = null;
    private Double expectedPrice = null;
    private Integer durationInMinutes = null;
    private Integer maxGroupSize = null;

    public static Presentation from(PresentationRequest presentationRequest, User speaker, Set<Tag> tags) {
        return new Presentation()
                .setSpeaker(speaker)
                .setTitle(presentationRequest.title())
                .setShortDescription(presentationRequest.shortDescription())
                .setDescription(presentationRequest.description())
                .setLevel(presentationRequest.level())
                .setLanguage(presentationRequest.language())
                .setTags(tags)
                .setStatus(STATUS_REPORTED);
    }

    public static Presentation from(WorkshopRequest workshopRequest, User speaker, Set<Tag> tags) {
        return new Presentation()
                .setSpeaker(speaker)
                .setTitle(workshopRequest.title())
                .setShortDescription(workshopRequest.shortDescription())
                .setDescription(workshopRequest.description())
                .setLevel(workshopRequest.level())
                .setLanguage(workshopRequest.language())
                .setTags(tags)
                .setWorkshop(true)
                .setIsFree(workshopRequest.isFree())
                .setExpectedPrice(workshopRequest.isFree() ? 0 : workshopRequest.expectedPrice())
                .setDurationInMinutes(workshopRequest.durationInMinutes())
                .setMaxGroupSize(workshopRequest.maxGroupSize())
                .setStatus(STATUS_REPORTED);
    }

    // Submit again (ADR-0004): the same talk as a fresh submission by one speaker, without the
    // co-speakers, review or edition it had. The caller sets the edition.
    public Presentation resubmittedBy(User speaker) {
        return new Presentation()
                .setSpeaker(speaker)
                .setTitle(title)
                .setShortDescription(shortDescription)
                .setDescription(description)
                .setLevel(level)
                .setLanguage(language)
                .setTags(new HashSet<>(tags))
                .setWorkshop(workshop)
                .setIsFree(isFree)
                .setExpectedPrice(expectedPrice)
                .setDurationInMinutes(durationInMinutes)
                .setMaxGroupSize(maxGroupSize)
                .setStatus(STATUS_REPORTED);
    }

    public boolean isOwnedBy(String email) {
        return speakers.stream().anyMatch(it -> it.getEmail().equalsIgnoreCase(email));
    }

    public boolean hasCospeaker(String email) {
        return speakers.stream().anyMatch(it -> it.getEmail().equalsIgnoreCase(email));
    }

    public boolean isAccepted() {
        return STATUS_ACCEPTED.equals(status);
    }

    public void setAccepted(boolean accepted) {
        if (accepted) {
            status = STATUS_ACCEPTED;
        } else {
            status = STATUS_REPORTED;
        }
    }

    public boolean isNew() {
        return id == null;
    }

    public Presentation setSpeaker(User speaker) {
        speakers.add(speaker);
        return this;
    }

    public void update(PresentationRequest presentationRequest, Set<Tag> tags) {
        this.tags = tags;
        this.title = presentationRequest.title();
        this.shortDescription = presentationRequest.shortDescription();
        this.description = presentationRequest.description();
        this.level = presentationRequest.level();
        this.language = presentationRequest.language();
    }

    public void update(WorkshopRequest workshopRequest, Set<Tag> tags) {
        this.tags = tags;
        this.title = workshopRequest.title();
        this.shortDescription = workshopRequest.shortDescription();
        this.description = workshopRequest.description();
        this.level = workshopRequest.level();
        this.language = workshopRequest.language();
        this.isFree = workshopRequest.isFree();
        this.expectedPrice = workshopRequest.isFree() ? 0 : workshopRequest.expectedPrice();
        this.durationInMinutes = workshopRequest.durationInMinutes();
        this.maxGroupSize = workshopRequest.maxGroupSize();

    }
}
