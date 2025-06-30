package com.taskmanager.app.todo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/todo")
@Tag(name = "Todo Items", description = "Operations related to to-do items")
public class TodoItemResource {

    private final TodoItemService todoItemService;

    public TodoItemResource(TodoItemService todoItemService) {
        this.todoItemService = todoItemService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a to-do item by UID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved to-do item"),
        @ApiResponse(responseCode = "404", description = "To-do item not found")
    })
    public ResponseEntity<TodoItemResponse> getTodoItem(@PathVariable("id") UUID todoItemUid) {
        try {
            TodoItemResponse todoItemResponse = todoItemService.getByUid(todoItemUid);
            return new ResponseEntity<>(todoItemResponse, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    @Operation(summary = "Get all to-do items")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved all to-do items")
    public ResponseEntity<List<TodoItemResponse>> getAllTodoItems() {
        List<TodoItemResponse> todoItemResponses = todoItemService.getAllTodoItems();
        return new ResponseEntity<>(todoItemResponses, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new to-do item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "To-do item created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<TodoItemResponse> createTodoItem(@RequestBody TodoItemCreationRequest todoItemDto) {
        TodoItemResponse createdTodoItem = todoItemService.create(todoItemDto);
        return new ResponseEntity<>(createdTodoItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a to-do item by UID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated to-do item"),
        @ApiResponse(responseCode = "404", description = "To-do item not found")
    })
    public ResponseEntity<TodoItemResponse> updateTodoItem(
            @PathVariable("id") UUID todoItemUid,
            @RequestBody TodoItemResponse todoItemResponse) {
        try {
            TodoItemResponse updatedTodoItem = todoItemService.update(todoItemUid, todoItemResponse);
            return new ResponseEntity<>(updatedTodoItem, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all to-do items by user UID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks for the user")
    public ResponseEntity<List<TodoItemResponse>> getAllTasksByUserId(@PathVariable UUID userId) {
        List<TodoItemResponse> todoItemResponses = todoItemService.getAllTasksByUserUid(userId);
        return new ResponseEntity<>(todoItemResponses, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a to-do item by UID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully deleted to-do item"),
        @ApiResponse(responseCode = "404", description = "To-do item not found")
    })
    public ResponseEntity<Void> deleteTodoItem(@PathVariable("id") UUID todoItemUid) {
        try {
            todoItemService.delete(todoItemUid);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
