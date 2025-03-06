package com.taskmanager.app.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/users")
public class TaskUserResource {

    private final TaskUserService taskUserService;

    public TaskUserResource(TaskUserService taskUserService) {
        this.taskUserService = taskUserService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCreationResponse> getUser(@PathVariable("id") Long userId) {
        UserCreationResponse userResponse = taskUserService.get(userId); // Service will handle exception
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserCreationResponse>> getAllUsers() {
        List<UserCreationResponse> userResponses = taskUserService.getAll();
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserCreationResponse> createUser(@RequestBody UserCreationRequest userRequest) {
        UserCreationResponse createdUser = taskUserService.create(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserCreationResponse> updateUser(@PathVariable("id") Long userId, 
                                                           @RequestBody UserCreationResponse userResponse) {
        try {
            UserCreationResponse updatedUser = taskUserService.update(userId, userResponse);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long userId) {
        try {
            taskUserService.delete(userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
