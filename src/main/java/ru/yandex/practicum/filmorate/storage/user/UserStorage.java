package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    // GET "/users"
    Collection<User> findAllUsers();

    // POST "/users"
    User saveUser(User user);

    // PUT "/users"
    User putUser(User user);

    // GET "/users/{id}"
    Optional<User> findUserById(Long id);

    // PUT /users/{id}/friends/{friendId}
    void addFriend(User user, User friend);

    // DELETE /users/{id}/friends/{friendId}
    void removeFriend(User user, User friend);

    // GET /users/{id}/friends
    List<User> findFriends(User user);

    // GET /users/{id}/friends/common/{otherId}
    List<User> findCommonFriends(User user, User otherUser);

    boolean hasUsersId(Long userId);
}
