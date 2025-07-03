package com.taskmanager.app.notifications;

import com.taskmanager.app.todo.TodoItem;

/**
 * Simple contract for sending notifications about {@link TodoItem}s.
 */
public interface NotificationService {

    /**
     * Send a notification that the given todo item is due soon.
     *
     * @param todoItem the item that will be due shortly
     */
    void sendDueSoonNotification(TodoItem todoItem);
}
