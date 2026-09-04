package pl.confitura.jelatyna.chat;

import org.springframework.http.HttpStatus;

/** Pre-flight rejection of a chat request, mapped to an HTTP status by the controller. */
public class ChatException extends RuntimeException {

    public enum Reason {
        DISABLED(HttpStatus.SERVICE_UNAVAILABLE),
        INVALID(HttpStatus.BAD_REQUEST),
        TOO_LONG(HttpStatus.PAYLOAD_TOO_LARGE),
        RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS),
        CAP_REACHED(HttpStatus.SERVICE_UNAVAILABLE);

        private final HttpStatus status;

        Reason(HttpStatus status) {
            this.status = status;
        }

        public HttpStatus status() {
            return status;
        }
    }

    private final Reason reason;

    public ChatException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }
}
