package pl.confitura.jelatyna.presentation;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "available_tags")
public class Tag {
    @Id
    private String id;
    private String name;

    public static Tag of(String tag) {
        return new Tag().setName(tag);
    }
}
