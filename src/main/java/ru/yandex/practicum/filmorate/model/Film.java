package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.serializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
}
