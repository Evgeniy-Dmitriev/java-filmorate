package ru.yandex.practicum.filmorate.dbStorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.RatingDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class, RatingDbStorage.class, GenreDbStorage.class})
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final RatingDbStorage mpaStorage;

    private Film testFilm;
    private User testUser;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setName("Test Film");
        testFilm.setDescription("Test Description");
        testFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testFilm.setDuration(Duration.ofMinutes(120));

        Optional<Rating> mpa = mpaStorage.findRatingById(1L);
        assertThat(mpa).isPresent();
        testFilm.setMpa(mpa.get());

        testUser = User.builder()
                .email("test@example.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        testUser = userStorage.saveUser(testUser);
    }

    @Test
    void testCreateFilm() {
        Film createdFilm = filmStorage.saveFilm(testFilm);

        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo("Test Film");
        assertThat(createdFilm.getDescription()).isEqualTo("Test Description");
        assertThat(createdFilm.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
        assertThat(createdFilm.getDuration()).isEqualTo(Duration.ofMinutes(120));
        assertThat(createdFilm.getMpa().getId()).isEqualTo(1L);
    }

    @Test
    void testGetFilmById() {
        Film createdFilm = filmStorage.saveFilm(testFilm);

        Optional<Film> retrievedFilm = filmStorage.findFilmById(createdFilm.getId());

        assertThat(retrievedFilm)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId()).isEqualTo(createdFilm.getId());
                    assertThat(film.getName()).isEqualTo("Test Film");
                    assertThat(film.getDescription()).isEqualTo("Test Description");
                    assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1));
                    assertThat(film.getDuration()).isEqualTo(Duration.ofMinutes(120));
                    assertThat(film.getMpa().getId()).isEqualTo(1L);
                });
    }

    @Test
    void testUpdateFilm() {
        Film createdFilm = filmStorage.saveFilm(testFilm);

        createdFilm.setName("Updated Name");
        createdFilm.setDescription("Updated Description");

        Film updatedFilm = filmStorage.updateFilm(createdFilm);

        assertThat(updatedFilm.getId()).isEqualTo(createdFilm.getId());
        assertThat(updatedFilm.getName()).isEqualTo("Updated Name");
        assertThat(updatedFilm.getDescription()).isEqualTo("Updated Description");

        Optional<Film> retrievedFilm = filmStorage.findFilmById(createdFilm.getId());
        assertThat(retrievedFilm)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getName()).isEqualTo("Updated Name");
                    assertThat(film.getDescription()).isEqualTo("Updated Description");
                });
    }

    @Test
    void testGetAllFilms() {
        filmStorage.saveFilm(testFilm);

        Film secondFilm = new Film();
        secondFilm.setName("Second Film");
        secondFilm.setDescription("Second Description");
        secondFilm.setReleaseDate(LocalDate.of(2010, 5, 5));
        secondFilm.setDuration(Duration.ofMinutes(90));
        secondFilm.setMpa(testFilm.getMpa()); // Используем тот же MPA
        filmStorage.saveFilm(secondFilm);

        List<Film> films = new ArrayList<>(filmStorage.findAllFilms());

        assertThat(films.size()).isEqualTo(2);
        assertThat(films.get(0).getName()).isEqualTo("Test Film");
        assertThat(films.get(1).getName()).isEqualTo("Second Film");
    }

    @Test
    void testDeleteFilm() {
        Film createdFilm = filmStorage.saveFilm(testFilm);

        boolean deleted = filmStorage.deleteFilmById(createdFilm.getId());

        assertThat(deleted).isTrue();

        Optional<Film> retrievedFilm = filmStorage.findFilmById(createdFilm.getId());
        assertThat(retrievedFilm).isEmpty();
    }

    @Test
    void testAddAndGetFilmWithGenres() {
        Set<Genre> genres = new HashSet<>();
        Genre genre1 = new Genre();
        genre1.setId(1L);
        genres.add(genre1);

        Genre genre2 = new Genre();
        genre2.setId(2L);
        genres.add(genre2);

        testFilm.setGenres(genres);

        Film createdFilm = filmStorage.saveFilm(testFilm);

        Optional<Film> retrievedFilm = filmStorage.findFilmById(createdFilm.getId());
        Set<Genre> retrievedGenres = retrievedFilm.orElse(new Film()).getGenres();

        assertThat(retrievedFilm).isPresent();
        assertThat(retrievedFilm.get().getGenres().size()).isEqualTo(2);
        assertThat(retrievedGenres.iterator().next().getId()).isEqualTo(1L);
    }

    @Test
    void testAddAndRemoveLike() {
        Film createdFilm = filmStorage.saveFilm(testFilm);

        filmStorage.addLike(createdFilm, testUser.getId());

        Optional<Film> filmWithLike = filmStorage.findFilmById(createdFilm.getId());
        assertThat(filmWithLike).isPresent();
        assertThat(filmWithLike.get().getLikes().size()).isEqualTo(1);
        assertThat(filmWithLike.get().getLikes().iterator().next()).isEqualTo(testUser.getId());

        filmStorage.removeLike(createdFilm, testUser.getId());

        Optional<Film> filmWithoutLike = filmStorage.findFilmById(createdFilm.getId());
        assertThat(filmWithoutLike).isPresent();
        assertThat(filmWithoutLike.get().getLikes().size()).isEqualTo(0);
    }

    @Test
    void testGetPopularFilms() {
        Film firstFilm = filmStorage.saveFilm(testFilm);

        Film secondFilm = new Film();
        secondFilm.setName("Second Film");
        secondFilm.setDescription("Second Description");
        secondFilm.setReleaseDate(LocalDate.of(2010, 5, 5));
        secondFilm.setDuration(Duration.ofMinutes(90));
        secondFilm.setMpa(testFilm.getMpa());
        Film createdSecond = filmStorage.saveFilm(secondFilm);

        User secondUser = User.builder()
                .email("second@example.com")
                .login("seconduser")
                .name("Second User")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
        secondUser = userStorage.saveUser(secondUser);

        filmStorage.addLike(firstFilm, testUser.getId());
        filmStorage.addLike(createdSecond, testUser.getId());
        filmStorage.addLike(createdSecond, secondUser.getId());

        List<Film> popularFilms = filmStorage.findMostPopularFilms(2);

        assertThat(popularFilms.size()).isEqualTo(2);
        assertThat(popularFilms.get(0).getId()).isEqualTo(createdSecond.getId());
        assertThat(popularFilms.get(1).getId()).isEqualTo(firstFilm.getId());
    }
}
