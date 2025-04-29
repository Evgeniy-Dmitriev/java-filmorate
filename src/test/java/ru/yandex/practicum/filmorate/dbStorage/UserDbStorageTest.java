package ru.yandex.practicum.filmorate.dbStorage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserDbStorage.class)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .login("testuser")
                .name("Test User")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void testCreateUser() {
        User createdUser = userStorage.saveUser(testUser);

        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo("test@example.com");
        assertThat(createdUser.getLogin()).isEqualTo("testuser");
        assertThat(createdUser.getName()).isEqualTo("Test User");
        assertThat(createdUser.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void testGetUserById() {
        User createdUser = userStorage.saveUser(testUser);

        Optional<User> retrievedUser = userStorage.findUserById(createdUser.getId());

        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getId()).isEqualTo(createdUser.getId());
                    assertThat(user.getEmail()).isEqualTo("test@example.com");
                    assertThat(user.getLogin()).isEqualTo("testuser");
                    assertThat(user.getName()).isEqualTo("Test User");
                    assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
                });
    }

    @Test
    void testUpdateUser() {
        User createdUser = userStorage.saveUser(testUser);

        createdUser.setName("Updated Name");
        createdUser.setEmail("updated@example.com");

        User updatedUser = userStorage.updateUser(createdUser);

        assertThat(updatedUser.getId()).isEqualTo(createdUser.getId());
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");

        Optional<User> retrievedUser = userStorage.findUserById(createdUser.getId());
        assertThat(retrievedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getName()).isEqualTo("Updated Name");
                    assertThat(user.getEmail()).isEqualTo("updated@example.com");
                });
    }

    @Test
    void testGetAllUsers() {
        userStorage.saveUser(testUser);

        User secondUser = User.builder()
                .email("second@example.com")
                .login("seconduser")
                .name("Second User")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
        userStorage.saveUser(secondUser);

        Collection<User> users = userStorage.findAllUsers();
        List<User> users1 = new ArrayList<>(users);

        assertThat(users1.size()).isEqualTo(2);
        assertThat(users1.get(0).getEmail()).isEqualTo("test@example.com");
        assertThat(users1.get(1).getEmail()).isEqualTo("second@example.com");
    }

    @Test
    void testDeleteUser() {
        User createdUser = userStorage.saveUser(testUser);

        boolean deleted = userStorage.deleteUserById(createdUser.getId());

        assertThat(deleted).isTrue();

        Optional<User> retrievedUser = userStorage.findUserById(createdUser.getId());
        assertThat(retrievedUser).isEmpty();
    }

    @Test
    void testAddAndGetFriends() {
        User firstUser = userStorage.saveUser(testUser);

        User secondUser = User.builder()
                .email("friend@example.com")
                .login("friend")
                .name("Friend User")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
        User createdFriend = userStorage.saveUser(secondUser);

        userStorage.addFriend(firstUser, createdFriend);

        List<User> friends = userStorage.findFriends(firstUser);

        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.get(0).getId()).isEqualTo(createdFriend.getId());
    }

    @Test
    void testRemoveFriend() {
        User firstUser = userStorage.saveUser(testUser);

        User secondUser = User.builder()
                .email("friend@example.com")
                .login("friend")
                .name("Friend User")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
        User createdFriend = userStorage.saveUser(secondUser);

        userStorage.addFriend(firstUser, createdFriend);

        userStorage.removeFriend(firstUser, createdFriend);

        List<User> friends = userStorage.findFriends(firstUser);
        assertThat(friends.size()).isEqualTo(0);
    }

    @Test
    void testGetCommonFriends() {
        User firstUser = userStorage.saveUser(testUser);

        User secondUser = User.builder()
                .email("second@example.com")
                .login("second")
                .name("Second User")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
        User createdSecond = userStorage.saveUser(secondUser);

        User commonFriend = User.builder()
                .email("common@example.com")
                .login("common")
                .name("Common Friend")
                .birthday(LocalDate.of(2000, 10, 10))
                .build();

        User createdCommon = userStorage.saveUser(commonFriend);

        userStorage.addFriend(firstUser, createdCommon);
        userStorage.addFriend(createdSecond, createdCommon);

        List<User> commonFriends = userStorage.findCommonFriends(firstUser, createdSecond);

        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(createdCommon.getId());
    }
}
