package pl.confitura.jelatyna.faq;

import org.junit.jupiter.api.Test;
import pl.confitura.jelatyna.faq.FaqMarkdownParser.ParsedFaqEntry;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FaqMarkdownParserTest {

    private final FaqMarkdownParser parser = new FaqMarkdownParser();

    @Test
    void splitsCategoriesQuestionsAndAnswers() {
        String md = """
                ## Registration
                ### How do I sign up?
                Buy a **ticket** on the site.

                It is quick.
                ### Is it free?
                No.
                ## Venue
                ### Where is it?
                At the venue in Warsaw.
                """;

        List<ParsedFaqEntry> entries = parser.parse(md);

        assertThat(entries).hasSize(3);
        assertThat(entries.get(0)).isEqualTo(new ParsedFaqEntry(
                "Registration", "How do I sign up?", "Buy a **ticket** on the site.\n\nIt is quick."));
        assertThat(entries.get(1)).isEqualTo(new ParsedFaqEntry("Registration", "Is it free?", "No."));
        assertThat(entries.get(2)).isEqualTo(new ParsedFaqEntry("Venue", "Where is it?", "At the venue in Warsaw."));
    }

    @Test
    void skipsMarkdownCommentsAndTopLevelTitle() {
        String md = """
                # FAQ
                ## Registration
                [//]: # (### Hidden question)
                ### Visible question
                Answer.
                """;

        List<ParsedFaqEntry> entries = parser.parse(md);

        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).question()).isEqualTo("Visible question");
        assertThat(entries.get(0).answer()).isEqualTo("Answer.");
    }

    @Test
    void preservesAnswerMarkdownAndReturnsEmptyForBlankInput() {
        assertThat(parser.parse(null)).isEmpty();
        assertThat(parser.parse("   ")).isEmpty();

        List<ParsedFaqEntry> entries = parser.parse("""
                ## General
                ### Q
                - one
                - two
                """);
        assertThat(entries).hasSize(1);
        assertThat(entries.get(0).answer()).isEqualTo("- one\n- two");
    }
}
