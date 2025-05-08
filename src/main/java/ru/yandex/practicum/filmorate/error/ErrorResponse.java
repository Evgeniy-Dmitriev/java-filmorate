package ru.yandex.practicum.filmorate.error;

public class ErrorResponse {

    private final String description;
    private final String error;

    public ErrorResponse(String description,String error) {
        this.error = error;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getError() {
        return error;
    }
}
