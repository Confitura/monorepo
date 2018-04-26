package pl.confitura.jelatyna.voting;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class PresentationStats {
    String title;
    String presentationId;

    Long positiveVotes = 0L;
    Long negativeVotes = 0L;
    Long totalVotes = 1L;

    PresentationStats(Vote vote) {
        title = vote.getPresentation().getTitle();
        presentationId = vote.getPresentation().getId();
        if (vote.getRate() > 0) {
            positiveVotes++;
        } else if (vote.getRate() < 0) {
            negativeVotes++;
        }
    }

    PresentationStats add(PresentationStats stats) {
        return new PresentationStats(
                title,
                presentationId,
                positiveVotes + stats.positiveVotes,
                negativeVotes + stats.negativeVotes,
                totalVotes + stats.totalVotes
        );
    }
}
