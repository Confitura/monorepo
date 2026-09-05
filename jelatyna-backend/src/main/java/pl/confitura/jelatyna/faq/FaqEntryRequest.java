package pl.confitura.jelatyna.faq;

/** Create/update payload for a FAQ entry; null fields are left unchanged on update. */
public record FaqEntryRequest(
        String category,
        String question,
        String answer,
        Integer displayOrder,
        Boolean published) {
}
