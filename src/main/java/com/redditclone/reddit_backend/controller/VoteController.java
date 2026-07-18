package com.redditclone.reddit_backend.controller;


import com.redditclone.reddit_backend.dtos.Vote.VoteRequest;
import com.redditclone.reddit_backend.dtos.Vote.VoteResponse;
import com.redditclone.reddit_backend.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vote", description = "Vote endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;



    @Operation(
            summary = "Vote on a post",
            description = "Allows the authenticated user to vote on a specific post by its unique identifier."
    )
    @PostMapping("/posts/{id}/vote")
    public ResponseEntity<VoteResponse> votePost(@PathVariable Long id, @Valid @RequestBody VoteRequest voteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(voteService.votePost(id, voteRequest));
    }



    @Operation(
            summary = "Vote on a comment",
            description = "Allows the authenticated user to vote on a specific comment by its unique identifier."
    )
    @PostMapping("/comments/{id}/vote")
    public ResponseEntity<VoteResponse> voteComment(@PathVariable Long id, @Valid @RequestBody VoteRequest voteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(voteService.voteComment(id, voteRequest));
    }



    @Operation(
            summary = "Get vote by ID",
            description = "Retrieves a specific vote by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<VoteResponse> getVoteById(@PathVariable Long id) {
        return ResponseEntity.ok(voteService.getVoteById(id));
    }



    @Operation(
            summary = "Get current user's vote for a post",
            description = "Retrieves the current authenticated user's vote for a specific post by its unique identifier."
    )
    @GetMapping("/posts/{postId}/vote")
    public ResponseEntity<VoteResponse> getCurrentUserVoteForPost(@PathVariable Long postId) {
        return ResponseEntity.ok(voteService.getCurrentUserVoteForPost(postId));
    }


    @Operation(
            summary = "Get current user's vote for a comment",
            description = "Retrieves the current authenticated user's vote for a specific comment by its unique identifier."
    )
    @GetMapping("/comments/{commentId}/vote")
    public ResponseEntity<VoteResponse> getCurrentUserVoteForComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(voteService.getCurrentUserVoteForComment(commentId));
    }


    @Operation(
            summary = "Delete a vote",
            description = "Deletes a vote by its unique identifier."

    )
    @DeleteMapping("/votes/{voteId}")
    public ResponseEntity<Void> deleteVote(@PathVariable Long voteId) {
        voteService.deleteVote(voteId);
        return ResponseEntity.noContent().build();
    }

}
