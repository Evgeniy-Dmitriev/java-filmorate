package ru.yandex.practicum.filmorate.dbStorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.RatingDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(RatingDbStorage.class)
class RatingDbStorageTest {

    private final RatingDbStorage ratingStorage;

    @Test
    void testGetAllMpa() {
        List<Rating> mpas = ratingStorage.findAllRatings();

        assertThat(mpas).isNotEmpty();
    }

    @Test
    void testGetMpaById() {
        Optional<Rating> mpa = ratingStorage.findRatingById(1L);

        assertThat(mpa)
                .isPresent()
                .hasValueSatisfying(m -> {
                    assertThat(m.getId()).isEqualTo(1L);
                    assertThat(m.getName()).isNotEmpty();
                });
    }

    @Test
    void testGetNonExistentMpa() {
        Optional<Rating> mpa = ratingStorage.findRatingById(999L);

        assertThat(mpa).isEmpty();
    }
}
