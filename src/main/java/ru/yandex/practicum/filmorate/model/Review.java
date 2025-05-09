package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    private Long reviewId;

    @NotEmpty
    @NotNull
    private String content;

    private Boolean isPositive;

    private Long userId;

    private Long filmId;

    private Integer useful = 0;

}
