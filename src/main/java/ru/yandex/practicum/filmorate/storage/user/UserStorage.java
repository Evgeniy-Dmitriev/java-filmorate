package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAllUsers();

    User saveUser(User user);

    User putUser(User user);

    Optional<User> findUserById(Long id);

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    List<User> findFriends(User user);

    List<User> findCommonFriends(User user, User otherUser);

    boolean hasUsersId(Long userId);
}
