package com.taskmanager.app.todo;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.taskmanager.app.user.TaskUser;
import com.taskmanager.app.user.TaskUserNotFoundException;
import com.taskmanager.app.user.TaskUserRepository;

@Service
public class TodoItemService {

    private final TodoItemRepository todoItemRepository;
    private final TaskUserRepository taskUserRepository;
    
    public TodoItemService(TodoItemRepository todoItemRepository, TaskUserRepository taskUserRepository) {
        this.todoItemRepository = todoItemRepository;
		this.taskUserRepository = taskUserRepository;
    }

    public TodoItemResponse get(Long todoItemId) {
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with id " + todoItemId));
        return convertTodoItemToDto(todoItem);
    }

    public TodoItemResponse create(TodoItemCreationRequest todoItemCreationRequest) {
        TodoItem todoItem = convertDtoToTodoItem(todoItemCreationRequest);
        System.out.println("Saving TodoItem: " + todoItem); // Log before saving
        TodoItem todoItemSaved = todoItemRepository.save(todoItem);
        return convertTodoItemToDto(todoItemSaved);
    }

    public List<TodoItemResponse> getAllTodoItems() {
        return todoItemRepository.findAll().stream()
                .map(this::convertTodoItemToDto)
                .collect(Collectors.toList());
    }

    public TodoItemResponse update(Long todoItemId, TodoItemResponse todoItemResponse) {
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with id " + todoItemId));
        
        todoItem.setDescription(todoItemResponse.getDescription());
        todoItem.setComplete(todoItemResponse.getComplete());
        todoItem.setDueBy(todoItemResponse.getDueBy());
        todoItem.setStatus(todoItemResponse.getStatus());
        
        TaskUser taskUser = taskUserRepository.findById(todoItemResponse.getUser())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with id " + todoItemResponse.getUser()));

        todoItem.setTaskUser(taskUser);

        
        if(todoItem.getComplete()==true) {
        	todoItem.setStatus(TodoStatus.COMPLETE);
        }

        TodoItem todoItemUpdated = todoItemRepository.save(todoItem);
        return convertTodoItemToDto(todoItemUpdated);
    }

    public void delete(Long todoItemId) {
        TodoItem todoItem = todoItemRepository.findById(todoItemId)
                .orElseThrow(() -> new TodoItemNotFoundException("Todo item not found with id " + todoItemId));
        
        todoItemRepository.delete(todoItem);
    }

    public TodoItemResponse convertTodoItemToDto(TodoItem todoItem) {
        TodoItemResponse todoItemResponse = new TodoItemResponse();
        todoItemResponse.setDescription(todoItem.getDescription());
        todoItemResponse.setComplete(todoItem.getComplete());
        todoItemResponse.setDueBy(todoItem.getDueBy());
        todoItemResponse.setStatus(todoItem.getStatus());
        todoItemResponse.setId(todoItem.getId());

        if (todoItem.getTaskUser() != null) {
            todoItemResponse.setUser(todoItem.getTaskUser().getId()); // Set user ID
        }

        return todoItemResponse;
    }

    public TodoItem convertDtoToTodoItem(TodoItemCreationRequest todoItemCreationRequest) {
        TodoItem todoItem = new TodoItem();
        todoItem.setDescription(todoItemCreationRequest.getDescription());
        todoItem.setDueBy(todoItemCreationRequest.getDueBy());

        TaskUser taskUser = taskUserRepository.findById(todoItemCreationRequest.getUser())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with id " + todoItemCreationRequest.getUser()));

        todoItem.setTaskUser(taskUser);

        return todoItem;
    }
    
    public TodoItem convertDtoToTodoItem(TodoItemResponse todoItemResponse) {
        TodoItem todoItem = new TodoItem();
        
        todoItem.setId(todoItemResponse.getId());
        todoItem.setDescription(todoItemResponse.getDescription());
        todoItem.setDueBy(todoItemResponse.getDueBy());
        todoItem.setComplete(todoItemResponse.getComplete());
        todoItem.setStatus(todoItemResponse.getStatus());
        
        TaskUser taskUser = taskUserRepository.findById(todoItemResponse.getUser())
                .orElseThrow(() -> new TaskUserNotFoundException("User not found with id " + todoItemResponse.getUser()));


        todoItem.setTaskUser(taskUser);
        
        return todoItem;
    }

   
}
