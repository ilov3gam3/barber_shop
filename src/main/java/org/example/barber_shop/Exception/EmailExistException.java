package org.example.barber_shop.Exception;

public class EmailExistException extends RuntimeException{
    public EmailExistException(String message) {
        super(message);
    }
}
