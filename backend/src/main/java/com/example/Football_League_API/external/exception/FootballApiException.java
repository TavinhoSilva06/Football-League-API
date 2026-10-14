package com.example.Football_League_API.external.exception;

public class FootballApiException extends RuntimeException {

    public FootballApiException(String message) {
        super(message);
    }

    public FootballApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public FootballApiException(Throwable cause) {
        super(cause);
    }
}
