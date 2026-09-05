package pl.confitura.jelatyna.partner;

/**
 * Create/update payload for a partner; null fields are left unchanged on update.
 * The logo is not set here — it is uploaded separately via POST /resources/partners/{id}.
 */
public record PartnerRequest(
        String name,
        String type,
        String www,
        String description,
        String orientation,
        Boolean published) {
}
