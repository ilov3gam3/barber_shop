package org.example.barber_shop.DTO;

import java.io.Serializable;

public record ApiResponse<T>(int status, String message, T payload) implements Serializable {
}
