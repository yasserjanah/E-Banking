package me.janah.ebankingbackend.exceptions;

public class UserNotExistsException extends Exception {

    public UserNotExistsException(String message) {
        super(message);
    }
}
