package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.EventDTO;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

    private EventMapper() {
    }

    public static EventDTO eventToDTO(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        return EventDTO.builder()
                .eventId(event.getEventId())
                .userId(event.getUserId())
                .timestamp(event.getTimestamp())
                .eventType(event.getEventType())
                .operation(event.getOperation())
                .entityId(event.getEntityId())
                .build();
    }

    public static Event dtoToEvent(EventDTO eventDTO) {
        if (eventDTO == null) {
            throw new IllegalArgumentException("EventDTO cannot be null");
        }

        return Event.builder()
                .eventId(eventDTO.getEventId())
                .userId(eventDTO.getUserId())
                .timestamp(eventDTO.getTimestamp())
                .eventType(eventDTO.getEventType())
                .operation(eventDTO.getOperation())
                .entityId(eventDTO.getEntityId())
                .build();
    }

    public static List<EventDTO> listEventsToListDto(Collection<Event> events) {
        return events.stream().map(EventMapper::eventToDTO).collect(Collectors.toList());
    }
}
