package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

public interface EventStorage {

    List<Event> getFeed(Long userId);

    void createEvent(Event event);
}