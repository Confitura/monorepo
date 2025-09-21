package pl.confitura.jelatyna.presentation.rating;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Subselect;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Immutable
@Subselect("SELECT p.id                                                   AS presentation_id,\n" +
           "       p.title                                                AS title,\n" +
           "\n" +
           "       COUNT(pr.id)                                           AS rating_count,\n" +
           "       ROUND(AVG(\n" +
           "                     case\n" +
           "                         when pr.value = 'AWESOME' then 5\n" +
           "                         when pr.value = 'GREAT' then 4\n" +
           "                         when pr.value = 'IT_WAS_FINE' then 3\n" +
           "                         when pr.value = 'BAD' then 2\n" +
           "                         when pr.value = 'TERRIBLE' then 1\n" +
           "                         else 0\n" +
           "                         end\n" +
           "             ), 2)                                            AS avg_rating,\n" +
           "\n" +
           "       jsonb_agg(jsonb_build_object(\n" +
           "               'comment', pr.comment,\n" +
           "               'value', pr.value\n" +
           "                 ))        AS ratings,\n" +
           "       (select jsonb_agg(jsonb_build_object(\n" +
           "               'name', u.name,\n" +
           "               'id', u.id))\n" +
           "        from presentation_speakers ps\n" +
           "                 join users u on u.id = ps.speakers_id\n" +
           "        where ps.presentations_id = p.id)                     AS speakers,\n" +
           "\n" +
           "\n" +
           "       COUNT(pr.id) filter ( where pr.value = 'AWESOME' )     AS rating_count_awesome,\n" +
           "       COUNT(pr.id) filter ( where pr.value = 'GREAT' )       AS rating_count_great,\n" +
           "       COUNT(pr.id) filter ( where pr.value = 'IT_WAS_FINE' ) AS rating_count_it_was_fine,\n" +
           "       COUNT(pr.id) filter ( where pr.value = 'BAD' )         AS rating_count_bad,\n" +
           "       COUNT(pr.id) filter ( where pr.value = 'TERRIBLE' )    AS rating_count_terrible\n" +
           "\n" +
           "FROM public.presentation AS p\n" +
           "         JOIN public.presentation_rate AS pr ON pr.presentation_id = p.id\n" +
           "\n" +
           "GROUP BY p.id,\n" +
           "         p.title,\n" +
           "         p.description,\n" +
           "         p.language,\n" +
           "         p.level,\n" +
           "         p.short_description,\n" +
           "         p.status,\n" +
           "         p.workshop,\n" +
           "         p.duration_in_minutes,\n" +
           "         p.expected_price,\n" +
           "         p.is_free,\n" +
           "         p.max_group_size\n" +
           "\n" +
           "ORDER BY avg_rating desc")
public class ViewPresentationRate {
    @Id
    private String presentationId;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<Rate> ratings;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<Speaker> speakers;

    private String title;
    private Long ratingCount;
    private BigDecimal avgRating;
    private Long ratingCountAwesome;
    private Long ratingCountGreat;
    private Long ratingCountItWasFine;
    private Long ratingCountBad;
    private Long ratingCountTerrible;

    record Speaker(String id, String name) {

    }
    record Rate(String value, String comment) {

    }
}