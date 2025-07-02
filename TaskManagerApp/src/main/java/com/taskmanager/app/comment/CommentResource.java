package com.taskmanager.app.comment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "Endpoints for managing comments")
public class CommentResource {

    private final CommentService commentService;

    public CommentResource(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    @Operation(summary = "Get all comments")
    @ApiResponse(responseCode = "200", description = "List of all comments returned")
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        List<CommentResponse> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentUid}")
    @Operation(summary = "Get a comment by UID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comment found"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<CommentResponse> getCommentByUid(@PathVariable UUID commentUid) {
        CommentResponse comment = commentService.getCommentByUid(commentUid);
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    @Operation(summary = "Create a new comment")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Comment created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentCreationRequest request) {
        CommentResponse created = commentService.createComment(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{commentUid}")
    @Operation(summary = "Update an existing comment")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<CommentResponse> updateComment(@PathVariable UUID commentUid,
                                                         @RequestBody CommentResponse commentResponse) {
        CommentResponse updated = commentService.updateComment(commentUid, commentResponse);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentUid}")
    @Operation(summary = "Delete a comment by UID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Comment removed successfully"),
        @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentUid) {
        commentService.deleteComment(commentUid);
        return ResponseEntity.noContent().build();
    }
}
