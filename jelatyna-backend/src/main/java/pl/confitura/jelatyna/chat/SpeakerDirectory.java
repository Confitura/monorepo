package pl.confitura.jelatyna.chat;

import java.util.Map;

/** Supplies the speaker id → name mapping used to rejoin identity at the edge. */
public interface SpeakerDirectory {

    /** @return map of opaque speaker id to display name. */
    Map<String, String> idToName();
}
