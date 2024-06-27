package dev.alexcoss.carservice.util.exception;

public class CsvFileNotFoundException extends RuntimeException {
    public CsvFileNotFoundException(String message) {
        super(message);
    }
}
