package com.taskmanager.app.todo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/todo")
public class TodoItemResource {

    private final TodoItemService todoItemService;

    public TodoItemResource(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoItemResponse> getTodoItem(@PathVariable("id") Long todoItemId) {
        try {
            TodoItemResponse todoItemResponse = todoItemService.get(todoItemId);
            return new ResponseEntity<>(todoItemResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<TodoItemResponse>> getAllTodoItems() {
        List<TodoItemResponse> todoItemResponses = todoItemService.getAllTodoItems();
        return new ResponseEntity<>(todoItemResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<TodoItemResponse> createTodoItem(@RequestBody TodoItemCreationRequest todoItemDto) {
        TodoItemResponse createdTodoItem = todoItemService.create(todoItemDto);
        return new ResponseEntity<>(createdTodoItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoItemResponse> updateTodoItem(@PathVariable("id") Long todoItemId, 
                                                      @RequestBody TodoItemResponse todoItemResponse) {
        try {
        	TodoItemResponse updatedTodoItem = todoItemService.update(todoItemId, todoItemResponse);
            return new ResponseEntity<>(updatedTodoItem, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoItem(@PathVariable("id") Long todoItemId) {
        try {
            todoItemService.delete(todoItemId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
