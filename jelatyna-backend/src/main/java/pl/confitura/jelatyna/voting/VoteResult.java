package pl.confitura.jelatyna.voting;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import pl.confitura.jelatyna.presentation.PreSelectionStatus;
import pl.confitura.jelatyna.presentation.Presentation;
import pl.confitura.jelatyna.presentation.Tag;
import pl.confitura.jelatyna.user.User;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

public record VoteResult(
        @Schema(requiredMode = REQUIRED) String presentationId,
        @Schema(requiredMode = REQUIRED) String title,
        @Schema(requiredMode = REQUIRED) List<Speaker> speakers,
        @Schema(requiredMode = REQUIRED) String flatSpeakers,
        @Schema(requiredMode = REQUIRED) boolean workshop,
        @Schema(requiredMode = REQUIRED) String flatTags,
        @Schema(requiredMode = REQUIRED) int total,
        @Schema(requiredMode = REQUIRED) int score,
        @Schema(requiredMode = REQUIRED) int positive,
        @Schema(requiredMode = REQUIRED) int negative,
        @Schema(requiredMode = REQUIRED) int neutral,
        @Schema(requiredMode = REQUIRED) int positivePercent,
        @Schema(requiredMode = REQUIRED) int negativePercent,
        @Schema(requiredMode = REQUIRED) int neutralPercent,
        @Schema(requiredMode = REQUIRED) PreSelectionStatus preSelectionStatus,
        @Schema(requiredMode = REQUIRED) Map<String, Integer> voterScores
) {

    static VoteResult from(Presentation presentation, List<Vote> votes, Set<String> selectedTokens) {
        int total = votes.size();
        int positive = (int) votes.stream().filter(v -> v.getRate() > 0).count();
        int negative = (int) votes.stream().filter(v -> v.getRate() < 0).count();
        int neutral = (int) votes.stream().filter(v -> v.getRate() == 0).count();
        int score = votes.stream().mapToInt(Vote::getRate).sum();

        Map<String, Integer> voterScores = votes.stream()
                .filter(v -> selectedTokens.contains(v.getToken()))
                .collect(Collectors.groupingBy(Vote::getToken, Collectors.summingInt(Vote::getRate)));

        return new VoteResult(
                presentation.getId(),
                presentation.getTitle(),
                presentation.getSpeakers().stream().map(user -> new Speaker(user.getId(), user.getName())).toList(),
                presentation.getSpeakers().stream().map(User::getName).collect(Collectors.joining(", ")),
                presentation.isWorkshop(),
                presentation.getTags().stream().map(Tag::getId).collect(Collectors.joining(", ")),
                total,
                score,
                positive,
                negative,
                neutral,
                percent(positive, total),
                percent(negative, total),
                percent(neutral, total),
                presentation.getPreSelectionStatus() == null ? PreSelectionStatus.NONE : presentation.getPreSelectionStatus(),
                voterScores
        );
    }

    private static int percent(int part, int total) {
        return total == 0 ? 0 : (int) Math.round(part * 100.0 / total);
    }

    public record Speaker(
            @Schema(requiredMode = REQUIRED) String id,
            @Schema(requiredMode = REQUIRED) String name
    ) {
    }
}
