/*
 * package com.taskmanager.app.notifications;
 * 
 * import com.taskmanager.app.todo.TodoItemRepository; import
 * com.taskmanager.app.user.TaskUserRepository; import
 * com.taskmanager.app.todo.TodoItem;
 * 
 * import org.springframework.scheduling.annotation.Scheduled; import
 * org.springframework.stereotype.Component;
 * 
 * import java.time.LocalDateTime; import java.util.List;
 * 
 * @Component public class DueDateNotifier {
 * 
 * private final TodoItemRepository todoItemRepository; private final
 * NotificationService notificationService;
 * 
 * public DueDateNotifier(TodoItemRepository todoItemRepository,
 * NotificationService notificationService) { this.todoItemRepository =
 * todoItemRepository; this.notificationService = notificationService; }
 * 
 * @Scheduled(cron = "0 0 * * * *") // Every hour public void notifyDueTasks() {
 * LocalDateTime now = LocalDateTime.now(); LocalDateTime threshold =
 * now.plusHours(6); // Notify 6 hours before due
 * 
 * List<TodoItem> dueSoon = todoItemRepository.findByDueDateBetween(now,
 * threshold); dueSoon.forEach(task ->
 * notificationService.sendDueSoonNotification(task)); } }
 */