package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storages;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserStorage storage = Storages.getUserStorage();

    @GetMapping
    public Collection<User> findAll() {
        return storage.getAllUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return storage.postUser(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return storage.putUser(newUser);
    }
}
