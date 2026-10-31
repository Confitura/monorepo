package pl.confitura.jelatyna.webpage.faq.web;

import java.util.List;

/** New order for FAQ entries: displayOrder is set to each id's position in this list. */
public record ReorderRequest(List<String> ids) {
}
