package pl.confitura.jelatyna.presentation;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(path = "tags")
public interface TagRepository extends Repository<Tag, String> {
    @RestResource(exported = false)
    Iterable<Tag> save(Iterable<Tag> iterable);

    Iterable<Tag> findAll();

}
