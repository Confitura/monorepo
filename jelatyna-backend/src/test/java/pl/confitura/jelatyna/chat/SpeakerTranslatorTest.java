package pl.confitura.jelatyna.chat;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SpeakerTranslatorTest {

    private final SpeakerTranslator translator = new SpeakerTranslator(Map.of(
            "spk-1", "Artur Laskowski",
            "spk-2", "Jan Kowalski"
    ));

    @Test
    void rewritesSpeakerNamesToIdsInbound() {
        String result = translator.namesToIds("What is Artur Laskowski presenting?");
        assertThat(result).contains("spk-1").doesNotContain("Artur Laskowski");
    }

    @Test
    void nameMatchingIsCaseInsensitive() {
        assertThat(translator.namesToIds("is ARTUR LASKOWSKI here")).contains("spk-1");
    }

    @Test
    void rewritesIdsToNamesOutbound() {
        String result = translator.idsToNames("The talk is by spk-1 and spk-2.");
        assertThat(result)
                .contains("Artur Laskowski")
                .contains("Jan Kowalski")
                .doesNotContain("spk-1")
                .doesNotContain("spk-2");
    }

    @Test
    void leavesTextWithNoKnownSpeakersUnchanged() {
        assertThat(translator.idsToNames("No speakers here")).isEqualTo("No speakers here");
        assertThat(translator.namesToIds("No speakers here")).isEqualTo("No speakers here");
    }

    @Test
    void emptyDirectoryIsSafe() {
        SpeakerTranslator empty = new SpeakerTranslator(Map.of());
        assertThat(empty.idsToNames("spk-1")).isEqualTo("spk-1");
        assertThat(empty.namesToIds("Artur Laskowski")).isEqualTo("Artur Laskowski");
    }
}
