package pl.confitura.jelatyna.faq;

/** Create/update payload for a FAQ category; null fields are left unchanged on update. */
public record FaqCategoryRequest(
        String name,
        Boolean published) {
}
