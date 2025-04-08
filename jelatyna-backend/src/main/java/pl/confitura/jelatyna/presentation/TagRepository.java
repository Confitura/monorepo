package pl.confitura.jelatyna.presentation;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends Repository<Tag, String> {
    Iterable<Tag> saveAll(Iterable<Tag> iterable);

    List<Tag> findAll();

    Optional<Tag> findById(String id);
}
