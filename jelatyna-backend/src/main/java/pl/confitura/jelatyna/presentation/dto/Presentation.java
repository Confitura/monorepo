package pl.confitura.jelatyna.presentation.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import pl.confitura.jelatyna.presentation.Tag;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class Presentation {

    private String id;
    private String title;
    private String shortDescription;
    private String description;
    private String level;
    private String language;
    private Set<Tag> tags = new HashSet<>();
    private String status;
    private boolean workshop = false;
}
