package com.redditclone.reddit_backend.controller;

import com.redditclone.reddit_backend.dtos.Post.CreatePostRequest;
import com.redditclone.reddit_backend.dtos.Post.PostResponse;
import com.redditclone.reddit_backend.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post", description = "Post endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Create a new post",
            description = "Creates a new post for the authenticated user.")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody CreatePostRequest postRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(postRequest));
    }


    @Operation(
            summary = "Update a post",
            description = "Updates an existing post for the authenticated user.")
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @Valid @RequestBody CreatePostRequest postRequest) {
        return ResponseEntity.ok(postService.updatePost(id, postRequest));
    }


    @Operation(
            summary = "Get a post by ID",
            description = "Retrieves a post by its unique identifier.")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }


    @Operation(
            summary = "Get all posts",
            description = "Retrieves a list of all posts.")
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }


    @Operation(
            summary = "Get posts by username",
            description = "Retrieves a list of posts by a specific user.")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(
            @PathVariable String username) {
        return ResponseEntity.ok(postService.getAllPostsByUser(username));
    }

    @Operation(
            summary = "Delete a post by ID",
            description = "Deletes a post by its unique identifier.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostById(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
