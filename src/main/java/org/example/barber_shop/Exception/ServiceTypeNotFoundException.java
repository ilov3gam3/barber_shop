package org.example.barber_shop.Exception;

public class ServiceTypeNotFoundException extends RuntimeException {
    public ServiceTypeNotFoundException(String message) {
        super(message);
    }
}
