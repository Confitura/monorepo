package pl.confitura.jelatyna.faq;

import java.util.ArrayList;
import java.util.List;

/**
 * Splits the legacy single-blob FAQ markdown into individual entries:
 * {@code ## } headings become categories, {@code ### } headings become questions,
 * and the text that follows a question (until the next heading) becomes its answer.
 */
public class FaqMarkdownParser {

    public record ParsedFaqEntry(String category, String question, String answer) {
    }

    public List<ParsedFaqEntry> parse(String markdown) {
        List<ParsedFaqEntry> entries = new ArrayList<>();
        if (markdown == null || markdown.isBlank()) {
            return entries;
        }

        String category = "General";
        String question = null;
        StringBuilder answer = new StringBuilder();

        for (String line : markdown.split("\n", -1)) {
            String trimmed = line.strip();
            if (trimmed.startsWith("[//]: #")) {
                // markdown comment (hidden content) — skip
                continue;
            }
            if (trimmed.startsWith("### ")) {
                flush(entries, category, question, answer);
                question = trimmed.substring(4).strip();
                answer.setLength(0);
            } else if (trimmed.startsWith("## ")) {
                flush(entries, category, question, answer);
                question = null;
                answer.setLength(0);
                category = trimmed.substring(3).strip();
            } else if (trimmed.startsWith("# ")) {
                // top-level title — ignore
            } else if (question != null) {
                answer.append(line).append("\n");
            }
        }
        flush(entries, category, question, answer);
        return entries;
    }

    private void flush(List<ParsedFaqEntry> entries, String category, String question, StringBuilder answer) {
        if (question != null) {
            entries.add(new ParsedFaqEntry(category, question, answer.toString().strip()));
        }
    }
}
