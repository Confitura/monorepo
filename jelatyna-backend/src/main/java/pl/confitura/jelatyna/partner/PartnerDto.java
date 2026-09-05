package pl.confitura.jelatyna.partner;

public record PartnerDto(
        String id,
        String name,
        String type,
        String www,
        String logo,
        String description,
        String orientation,
        boolean published) {

    public static PartnerDto from(Partner p) {
        return new PartnerDto(
                p.getId(),
                p.getName(),
                p.getType(),
                p.getWww(),
                p.getLogo(),
                p.getDescription(),
                p.getOrientation(),
                p.isPublished());
    }
}
