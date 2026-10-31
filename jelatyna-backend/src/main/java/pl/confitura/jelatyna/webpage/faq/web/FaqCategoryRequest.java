package pl.confitura.jelatyna.webpage.faq.web;

/** Create/update payload for a FAQ category; null fields are left unchanged on update. */
public record FaqCategoryRequest(
        String name,
        Boolean published) {
}
