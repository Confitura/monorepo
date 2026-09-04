package pl.confitura.jelatyna.chat;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rejoins speaker identity at the edge so Datalinks only ever sees opaque ids.
 * <ul>
 *   <li>Inbound: rewrites speaker names in a question to their ids.</li>
 *   <li>Outbound: rewrites speaker ids in an answer back to names.</li>
 * </ul>
 * Name matching handles nominative forms only; Polish inflected forms are not
 * matched in this MVP.
 */
public class SpeakerTranslator {

    private final Map<String, String> idToName;
    private final List<Map.Entry<Pattern, String>> nameToIdPatterns;
    private final Pattern idPattern;

    public SpeakerTranslator(Map<String, String> idToName) {
        this.idToName = idToName;
        // Longer names first so "Jan Kowalski" wins over a bare "Jan".
        this.nameToIdPatterns = idToName.entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<String, String> e) -> e.getValue().length()).reversed())
                .map(e -> Map.entry(
                        Pattern.compile(Pattern.quote(e.getValue()), Pattern.CASE_INSENSITIVE),
                        e.getKey()))
                .toList();
        this.idPattern = idToName.isEmpty()
                ? null
                : Pattern.compile(idToName.keySet().stream()
                        .map(Pattern::quote)
                        .reduce((a, b) -> a + "|" + b)
                        .orElse(""));
    }

    /** Replace known speaker names with their opaque ids before sending upstream. */
    public String namesToIds(String question) {
        if (question == null) {
            return null;
        }
        String result = question;
        for (Map.Entry<Pattern, String> entry : nameToIdPatterns) {
            result = entry.getKey().matcher(result).replaceAll(Matcher.quoteReplacement(entry.getValue()));
        }
        return result;
    }

    /** Replace opaque ids in generated text with speaker names before returning. */
    public String idsToNames(String text) {
        if (text == null || idPattern == null) {
            return text;
        }
        Matcher matcher = idPattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String name = idToName.getOrDefault(matcher.group(), matcher.group());
            matcher.appendReplacement(sb, Matcher.quoteReplacement(name));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
