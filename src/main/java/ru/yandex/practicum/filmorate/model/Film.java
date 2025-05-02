package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.yandex.practicum.filmorate.serializer.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
    private Set<Long> likes = new HashSet<>();
    private Rating mpa;
    private Set<Genre> genres = new HashSet<>();
}
