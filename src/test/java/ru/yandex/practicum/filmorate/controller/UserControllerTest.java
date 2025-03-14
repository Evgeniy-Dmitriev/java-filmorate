package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    void testFindAll() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.create(user);
        List<User> users = new ArrayList<>(userController.findAll());

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("ivan@example.com", users.get(0).getEmail());
    }

    @Test
    void testCreateValidUser() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userController.create(user);

        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals("Иван Иванов", createdUser.getName());
    }

    @Test
    void testUpdateValidUser() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userController.create(user);

        User user2 = User.builder()
                .id(1)
                .email("ivan@example.com")
                .login("user321")
                .name("Пётр Петров")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        userController.update(user2);

        assertNotNull(createdUser);
        assertEquals(1, createdUser.getId());
        assertEquals("Пётр Петров", createdUser.getName());
        assertEquals("user321", createdUser.getLogin());
    }

    @Test
    void testEmptyLogin() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertTrue(exception.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void testNullLogin() {
        User user = User.builder()
                .email("ivan@example.com")
                .login(null)
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertTrue(exception.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void testLoginWithSpaces() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user 123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertTrue(exception.getMessage().contains("Логин не может быть пустым и содержать пробелы"));
    }

    @Test
    void testEmptyEmail() {
        User user = User.builder()
                .email("")
                .login("user 123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void testNullEmail() {
        User user = User.builder()
                .email(null)
                .login("user 123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void testEmailWithoutAtSign() {
        User user = User.builder()
                .email("invalid-email")
                .login("user 123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой и должна содержать символ @"));
    }

    @Test
    void testEmptyName() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userController.create(user);

        assertNotNull(createdUser);
        assertEquals("user123", createdUser.getName());
    }

    @Test
    void testNullName() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name(null)
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User createdUser = userController.create(user);

        assertNotNull(createdUser);
        assertEquals("user123", createdUser.getName());
    }

    @Test
    void testBirthdayInFuture() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.now().plusDays(1))
                .build();

        Exception exception = assertThrows(ValidationException.class, () -> {
            userController.create(user);
        });

        assertTrue(exception.getMessage().contains("Дата рождения не может быть в будущем"));
    }

    @Test
    void testBirthdayToday() {
        User user = User.builder()
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.now())
                .build();

        User createdUser = userController.create(user);

        assertNotNull(createdUser);
        assertEquals(LocalDate.now(), createdUser.getBirthday());
    }

    @Test
    void testUpdateNonExistingUser() {
        User user = User.builder()
                .id(999)
                .email("ivan@example.com")
                .login("user123")
                .name("Иван Иванов")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Exception exception = assertThrows(NotFoundException.class, () -> {
            userController.update(user);
        });

        assertTrue(exception.getMessage().contains("Пользователь с id = " + user.getId() + " не найден"));
    }
}