package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/directors")
    public List<Director> getDirectors() {
        log.info("Запрос на получение режиссеров");
        return directorService.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirector(@PathVariable("id") Long id) {
        log.info("Запрос на получение режиссера с id:{}",id);
        return directorService.getDirectorById(id);
    }

    @PostMapping("/directors")
    public Director createDirector(@RequestBody Director director) {
        log.info("Запрос на создание режиссера:{}", director);
        return directorService.createDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@RequestBody Director director) {
        log.info("Запрос на обновление режиссера:{}", director);
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    public boolean deleteDirector(@PathVariable Long id) {
        log.info("Запрос на удаление режиссера с id:{}", id);
        return directorService.deleteDirector(id);
    }
}
