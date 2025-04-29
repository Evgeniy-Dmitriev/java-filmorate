package ru.yandex.practicum.filmorate.dbStorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(GenreDbStorage.class)
class GenreDbStorageTest {

    private final GenreDbStorage genreStorage;

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreStorage.findAllGenres();

        assertThat(genres).isNotEmpty();
    }

    @Test
    void testGetGenreById() {
        Optional<Genre> genre = genreStorage.findGenreById(1L);

        assertThat(genre).isPresent();
        assertThat(genre.get().getId()).isEqualTo(1L);
        assertThat(genre.get().getName()).isNotEmpty();
    }

    @Test
    void testGetNonExistentGenre() {
        // Пытаемся получить несуществующий жанр
        Optional<Genre> genre = genreStorage.findGenreById(999L);

        assertThat(genre).isEmpty();
    }
}
