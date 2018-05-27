package io.github.jlameira.encoding;

import org.json.JSONException;

public class ZencoderException extends RuntimeException {

    public ZencoderException(String message) {
        super(message);
    }

    public ZencoderException(String message, Throwable cause) {
        super(message, cause);
    }
    public ZencoderException(String message, JSONException cause) {
        super(message, cause);
    }

}