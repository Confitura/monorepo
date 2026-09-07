package pl.confitura.jelatyna.faq;

/** Admin view of a FAQ category (without audit fields). */
public record FaqCategoryDto(
        String id,
        String name,
        int displayOrder,
        boolean published) {

    public static FaqCategoryDto from(FaqCategory c) {
        return new FaqCategoryDto(c.getId(), c.getName(), c.getDisplayOrder(), c.isPublished());
    }
}
