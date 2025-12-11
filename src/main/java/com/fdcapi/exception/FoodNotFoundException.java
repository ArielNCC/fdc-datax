package com.fdcapi.exception;

public class FoodNotFoundException extends RuntimeException {
    public FoodNotFoundException(String message) {
        super(message);
    }

    public FoodNotFoundException(Long foodId) {
        super("No se encontró alimento con ID: " + foodId);
    }
}
