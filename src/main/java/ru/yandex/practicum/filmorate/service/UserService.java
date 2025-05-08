package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.findAllUsers();
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String message = "Электронная почта не может быть пустой и должна содержать символ @";
            log.error("Failed to create user: {}", message);
            throw new ValidationException(message);
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String message = "Логин не может быть пустым и содержать пробелы";
            log.error("Failed to create user: {}", message);
            throw new ValidationException(message);
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть в будущем";
            log.error("Failed to create user: {}", message);
            throw new ValidationException(message);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, в качестве имени будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        return userStorage.saveUser(user);
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            String message = "Id должен быть указан";
            log.error("Ошибка при обновлении фильма: {}", message);
            throw new ConditionsNotMetException(message);
        }
        if (userStorage.hasUsersId(newUser.getId())) {
            if (!newUser.getEmail().contains("@")) {
                throw new ValidationException("Электронная почта должна содержать символ @");
            }
            if (newUser.getLogin().contains(" ")) {
                throw new ValidationException("Логин не может содержать пробелы");
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            return userStorage.updateUser(newUser);
        }
        log.error("Пользователь с id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    public boolean deleteUserById(Long id) {
        if (id == null || id < 1) throw new IllegalArgumentException("Invalid User id");
        return userStorage.deleteUserById(id);
    }

    public User getUserById(Long id) {
        return userStorage.findUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с id: " + friendId + " не найден"));

        userStorage.addFriend(user, friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг с id: " + friendId + " не найден"));

        userStorage.removeFriend(user, friend);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        return userStorage.findFriends(user);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        User otherUser = userStorage.findUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Другой пользователь с id: " + otherId + " не найден"));

        return userStorage.findCommonFriends(user, otherUser);
    }

    public List<Film> getRecommendations(Long userId) {
        User targetUser = getUserById(userId);

        Set<Long> targetLikes = new HashSet<>(filmStorage.findFilmLikes(targetUser));

        Collection<User> allUsers = getAllUsers().stream()
                .filter(u -> !u.getId().equals(userId))
                .toList();

        Map<User, Integer> similarityMap = new HashMap<>();

        for (User otherUser : allUsers) {
            Set<Long> otherLikes = new HashSet<>(filmStorage.findFilmLikes(otherUser));
            Set<Long> intersection = new HashSet<>(targetLikes);
            intersection.retainAll(otherLikes);
            similarityMap.put(otherUser, intersection.size());
        }

        if (similarityMap.isEmpty()) {
            return Collections.emptyList();
        }

        int maxSimilarity = similarityMap.values().stream().max(Integer::compareTo).orElse(0);

        if (maxSimilarity == 0) {
            return Collections.emptyList();
        }

        List<User> mostSimilarUsers = similarityMap.entrySet().stream()
                .filter(e -> e.getValue() == maxSimilarity)
                .map(Map.Entry::getKey)
                .toList();

        Set<Long> recommendedFilmIds = new HashSet<>();
        for (User similarUser : mostSimilarUsers) {
            Set<Long> likes = filmStorage.findFilmLikes(similarUser);
            likes.removeAll(targetLikes); // Только те, которых нет у target
            recommendedFilmIds.addAll(likes);
        }

        return recommendedFilmIds.stream()
                .map(filmStorage::findFilmById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
