package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {

    private long eventId;
    private long userId;
    private long timestamp;
    private EventType eventType;
    private Operation operation;
    private long entityId;
}

