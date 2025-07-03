package com.taskmanager.app.notifications;

import com.taskmanager.app.todo.TodoItem;
import com.taskmanager.app.todo.TodoItemRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled component that notifies users about tasks that are due soon.
 */
@Component
public class DueDateNotifier {

    private final TodoItemRepository todoItemRepository;
    private final NotificationService notificationService;

    public DueDateNotifier(
            TodoItemRepository todoItemRepository,
            NotificationService notificationService) {
        this.todoItemRepository = todoItemRepository;
        this.notificationService = notificationService;
    }

    /**
     * Runs every hour and notifies about tasks due within the next six hours.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void notifyDueTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusHours(6);

        List<TodoItem> dueSoon = todoItemRepository.findByDueByBetween(now, threshold);
        dueSoon.forEach(notificationService::sendDueSoonNotification);
    }
}
