package pl.confitura.jelatyna.presentation;

import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.api.model.WorkshopRequest;
import pl.confitura.jelatyna.presentation.rating.Rate;
import pl.confitura.jelatyna.api.model.PresentationRequest;
import pl.confitura.jelatyna.user.PublicUser;
import pl.confitura.jelatyna.user.User;

@Entity
@Data
@ToString(exclude = {"speakers", "ratings"})
@EqualsAndHashCode(exclude = {"speakers", "ratings"})
@Accessors(chain = true)
public class Presentation {

    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REPORTED = "reported";

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
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

    @ManyToMany
    @NotNull
    private Set<User> speakers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "presentation")
    private Set<Rate> ratings = new HashSet<>();

    private String status;

    private boolean workshop = false;

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

    boolean isOwnedBy(String email) {
        return speakers.stream().anyMatch(it -> it.getEmail().equalsIgnoreCase(email));
    }

    boolean hasCospeaker(String email) {
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

    @JsonIgnore
    public Set<PublicUser> getPublicSpeakers() {
        if (getSpeakers().isEmpty()) {
            return Collections.emptySet();
        } else {
            return getSpeakers().stream()
                    .map(PublicUser::new)
                    .collect(toSet());
        }
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
