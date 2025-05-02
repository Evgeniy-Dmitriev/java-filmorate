package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingController {

    private final RatingService mpaService;

    @Autowired
    public RatingController(RatingService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Rating> getAllMpa() {
        return mpaService.getAllRatings();
    }

    @GetMapping("/{id}")
    public Rating getMpaById(@PathVariable Long id) {
        return mpaService.getRatingById(id);
    }
}
