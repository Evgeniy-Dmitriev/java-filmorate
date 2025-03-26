package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    // GET "/users"
    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    // POST "/users"
    @Override
    public User saveUser(User user) {
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    // PUT "/users"
    @Override
    public User putUser(User newUser) {
        User oldUser = users.get(newUser.getId());
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
        }
        log.info("Пользователь обновлён: {}", oldUser);
        return oldUser;
    }

    // GET "/users/{id}"
    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    // PUT /users/{id}/friends/{friendId}
    @Override
    public void addFriend(User user, User friend) {
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        log.info("Добавлен друг {} пользователю {}", friend, user);
    }

    // DELETE /users/{id}/friends/{friendId}
    @Override
    public void removeFriend(User user, User friend) {
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
        log.info("У пользователя {} удалён друг {}", user, friend);
    }

    // GET /users/{id}/friends
    @Override
    public List<User> findFriends(User user) {
        log.info("Получен список друзей - {} человек пользователя {}", user.getFriends().size(), user);
        return user.getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    // GET /users/{id}/friends/common/{otherId}
    @Override
    public List<User> findCommonFriends(User user, User otherUser) {
        log.info("Получен список общих друзей пользователей {}, {}", user, otherUser);
        return user.getFriends()
                .stream()
                .filter(otherUser.getFriends()::contains)
                .map(this::findUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUsersId(Long userId) {
        return users.containsKey(userId);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
