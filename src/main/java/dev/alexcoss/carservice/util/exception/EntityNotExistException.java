package dev.alexcoss.carservice.util.exception;

public class EntityNotExistException extends RuntimeException{
    public EntityNotExistException(String message) {
        super(message);
    }
}
