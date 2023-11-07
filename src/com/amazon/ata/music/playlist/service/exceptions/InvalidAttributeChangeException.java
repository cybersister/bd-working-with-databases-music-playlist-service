package com.amazon.ata.music.playlist.service.exceptions;

public class InvalidAttributeChangeException extends InvalidAttributeException {

    private static final long serialVersionUID = -218424051070950665L;

    public InvalidAttributeChangeException() {
        super();
    }

    public InvalidAttributeChangeException(String message) {
        super(message);
    }

    public InvalidAttributeChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAttributeChangeException(Throwable cause) {
        super(cause);
    }

}
