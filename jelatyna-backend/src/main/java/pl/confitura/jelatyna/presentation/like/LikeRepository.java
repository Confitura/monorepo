package pl.confitura.jelatyna.presentation.like;

import org.springframework.data.repository.Repository;
import pl.confitura.jelatyna.presentation.Presentation;

import java.util.List;
import java.util.stream.Stream;

interface LikeRepository extends Repository<Like, String> {

    Like findByPresentationAndToken(Presentation id, String token);

    Like save(Like like);

    void deleteById(String likeId);

    Long countByPresentation(Presentation presentation);

    Stream<Like> findAll();

    List<Like> findByToken(String token);
}
