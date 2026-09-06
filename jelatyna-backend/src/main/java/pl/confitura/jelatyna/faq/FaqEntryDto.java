package pl.confitura.jelatyna.faq;

/** Public/admin view of a FAQ entry (without audit fields). */
public record FaqEntryDto(
        String id,
        String category,
        String question,
        String answer,
        int displayOrder,
        boolean published) {

    public static FaqEntryDto from(FaqEntry e) {
        return new FaqEntryDto(
                e.getId(),
                e.getCategory(),
                e.getQuestion(),
                e.getAnswer(),
                e.getDisplayOrder(),
                e.isPublished());
    }
}
