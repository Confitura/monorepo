package pl.confitura.jelatyna.presentation;

import org.springframework.data.repository.Repository;

public interface TagRepository extends Repository<Tag, String> {
    Iterable<Tag> saveAll(Iterable<Tag> iterable);

    Iterable<Tag> findAll();

}
