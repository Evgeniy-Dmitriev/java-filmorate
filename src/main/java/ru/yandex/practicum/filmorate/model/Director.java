package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Director {
    private Long id;
    @NotNull
    @NotEmpty
    private String name;
}
