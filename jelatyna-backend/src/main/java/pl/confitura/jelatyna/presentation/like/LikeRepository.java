package pl.confitura.jelatyna.presentation.like;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.stream.Stream;

interface LikeRepository extends Repository<Like, String> {

    Like findByPresentationIdAndToken(String presentationId, String token);

    Like save(Like like);

    void deleteById(String likeId);

    Long countByPresentationId(String presentationId);

    Stream<Like> findAll();

    List<Like> findByToken(String token);
}
