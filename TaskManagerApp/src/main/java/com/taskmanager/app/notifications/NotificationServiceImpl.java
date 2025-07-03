package com.taskmanager.app.notifications;

import com.taskmanager.app.todo.TodoItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Default implementation that publishes reminders to a Kafka topic.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String reminderTopic;

    public NotificationServiceImpl(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.reminder-topic}") String reminderTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.reminderTopic = reminderTopic;
    }

    @Override
    public void sendDueSoonNotification(TodoItem todoItem) {
        String message = "Reminder: Task \"" + todoItem.getTaskName()
                + "\" is due soon at " + todoItem.getDueBy();
        kafkaTemplate.send(reminderTopic, message);
    }
}
