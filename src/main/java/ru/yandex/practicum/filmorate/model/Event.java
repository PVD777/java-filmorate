package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Event {
    private int eventId;
    private long timestamp;
    private int userId;
    private EventTypes eventType;
    private OperationTypes operation;
    private int entityId;

    public Event(
            int userId,
            EventTypes eventType,
            OperationTypes operation,
            int entityId
    ) {
        this.timestamp = Timestamp.valueOf(LocalDateTime.now()).getTime();
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
