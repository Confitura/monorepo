package pl.confitura.jelatyna.presentation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import pl.confitura.jelatyna.presentation.rating.Rate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@ToString(exclude = { "speakers", "ratings" })
@EqualsAndHashCode(exclude = { "speakers", "ratings" })
@Accessors(chain = true)
class Presentation {

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
    @Column(length = 300)
    private String shortDescription;
    @NotBlank()
    @Column(length = 1000)
    private String description;
    @NotBlank
    private String level;
    @NotBlank
    private String language;
    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @NotNull
    private Set<Speaker> speakers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Rate> ratings = new HashSet<>();

    private String status;

    private boolean workshop = false;

    boolean isOwnedBy(String email) {
        return speakers.stream().anyMatch(it -> it.getEmail().equalsIgnoreCase(email));
    }

    public boolean ownerHasId(String id) {
        return speakers.stream().anyMatch(it -> it.getId().equals(id));
    }

    void setAccepted(boolean accepted) {
        if (accepted) {
            status = STATUS_ACCEPTED;
        } else {
            status = STATUS_REPORTED;
        }
    }

    public boolean isNew() {
        return id == null;
    }

    public Presentation setSpeaker(Speaker speaker) {
        speakers.add(speaker);
        return this;
    }

    pl.confitura.jelatyna.presentation.dto.Presentation toDto() {
        pl.confitura.jelatyna.presentation.dto.Presentation presentation = new pl.confitura.jelatyna.presentation.dto.Presentation();
        presentation.setId(id);
        presentation.setTitle(title);
        presentation.setShortDescription(shortDescription);
        presentation.setDescription(description);
        presentation.setLevel(level);
        presentation.setLanguage(language);
        presentation.setTags(tags);
        presentation.setStatus(status);
        presentation.setWorkshop(workshop);
        return presentation;
    }

    static Presentation fromDto(pl.confitura.jelatyna.presentation.dto.Presentation presentationDto) {
        Presentation presentation = new Presentation();
        presentation.setId(presentationDto.getId());
        presentation.setTitle(presentationDto.getTitle());
        presentation.setShortDescription(presentationDto.getShortDescription());
        presentation.setDescription(presentationDto.getDescription());
        presentation.setLevel(presentationDto.getLevel());
        presentation.setLanguage(presentationDto.getLanguage());
        presentation.setTags(presentationDto.getTags());
        presentation.setStatus(presentationDto.getStatus());
        presentation.setWorkshop(presentationDto.isWorkshop());
        return presentation;
    }
}
