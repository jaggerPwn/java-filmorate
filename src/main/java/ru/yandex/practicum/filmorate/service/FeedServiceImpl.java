package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDTO;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.util.List;

@Service
@Slf4j
public class FeedServiceImpl implements FeedService {

    private final FeedStorage feedStorage;
    private final UserService userService;

    @Autowired
    public FeedServiceImpl(FeedStorage feedStorage, @Lazy UserService userService) {
        this.feedStorage = feedStorage;
        this.userService = userService;
    }

    @Override
    public List<EventDTO> getFeed(long id) {
        userService.getUserById(id); // для валидации
        return EventMapper.listEventsToListDto(feedStorage.getFeed(id));
    }

    @Override
    public EventDTO saveFeed(long userId, long timestamp, EventType eventType, Operation operation, long entityId) {
        Event eventToSave = Event.builder()
                .userId(userId)
                .timestamp(timestamp)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();
        log.debug("В ленту пользователя с ID {} добавлено событие типа - {} операция - {}.", userId,
                eventType.toString(), operation.toString());
        return EventMapper.eventToDTO(feedStorage.saveFeed(eventToSave));
    }
}
