package com.redditclone.reddit_backend.controller;


import com.redditclone.reddit_backend.dtos.Subreddit.ChangeRoleRequest;
import com.redditclone.reddit_backend.dtos.Subreddit.SubredditMemberResponse;
import com.redditclone.reddit_backend.dtos.Subreddit.SubredditRequest;
import com.redditclone.reddit_backend.dtos.Subreddit.SubredditResponse;
import com.redditclone.reddit_backend.service.SubredditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Subreddit", description = "Subreddit endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/subreddits")
@RequiredArgsConstructor
public class SubredditController {

    private final SubredditService subredditService;

    @Operation(
            summary = "Create a new subreddit",
            description = "Creates a new subreddit for the authenticated user.")
    @PostMapping
    public ResponseEntity<SubredditResponse> createSubreddit(@Valid @RequestBody SubredditRequest subredditRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subredditService.createSubreddit(subredditRequest));
    }


    @Operation(
            summary = "Update a subreddit",
            description = "Updates an existing subreddit for the authenticated user.")
    @PutMapping("/{id}")
    public ResponseEntity<SubredditResponse> updateSubreddit(@PathVariable Long id, @Valid @RequestBody SubredditRequest subredditRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subredditService.updateSubreddit(id, subredditRequest));
    }




    @Operation(
            summary = "Get a subreddit by ID",
            description = "Retrieves a subreddit by its ID."
    )
    @GetMapping("/{id}")
    public ResponseEntity<SubredditResponse> getSubredditById(@PathVariable Long id) {
        return ResponseEntity.ok(subredditService.getSubredditById(id));
    }


    @Operation(
            summary = "Get a all subreddits",
            description = "Retrieves a list of all subreddits."
    )
    @GetMapping
    public  ResponseEntity<List<SubredditResponse>> getAllSubreddits() {

        return ResponseEntity.ok(subredditService.getAllSubreddits());
    }


    @Operation(
            summary = "Delete a subreddit",
            description = "Deletes an existing subreddit for the authenticated user."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubreddit(@PathVariable Long id) {
        subredditService.deleteSubreddit(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Join a subreddit",
            description = "Joins the authenticated user to an existing subreddit."
    )
    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinSubreddit(@PathVariable Long id) {
        subredditService.joinSubreddit(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Leave a subreddit",
            description = "Removes the authenticated user from an existing subreddit."
    )
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveSubreddit(@PathVariable Long id) {
        subredditService.leaveSubreddit(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Get members of a subreddit",
            description = "Retrieves a list of members of a specific subreddit."
    )
    @GetMapping("/{id}/members")
    public ResponseEntity<List<SubredditMemberResponse>> getMembersBySubreddit(@PathVariable Long id) {
        return ResponseEntity.ok(subredditService.getMembers(id));
    }


    @Operation(
            summary = "Change member role in a subreddit",
            description = "Changes the role of a member in a specific subreddit."
    )
    @PutMapping("/{id}/members/{memberId}/role")
    public ResponseEntity<SubredditMemberResponse> changeMemberRole(@PathVariable long id,@PathVariable long memberId,@Valid @RequestBody ChangeRoleRequest changeRoleRequest) {
        return ResponseEntity.ok(subredditService.changeMemberRole(id,memberId,changeRoleRequest));

    }


    @Operation(
            summary = "Remove a member from a subreddit",
            description = "Removes a member from a specific subreddit."
    )
    @DeleteMapping("/{id}/members/{memberId}")
    public  ResponseEntity<Void> removeMember(@PathVariable long id,@PathVariable long memberId) {
        subredditService.removeMember(id,memberId);
        return ResponseEntity.noContent().build();
    }




    @Operation(
            summary = "Search subreddits",
            description = "Searches for subreddits based on a query."
    )
    @GetMapping("/search")
    public ResponseEntity<List<SubredditResponse>> searchSubreddits(@RequestParam String query) {
        return ResponseEntity.ok(subredditService.searchSubreddits(query));
    }

}
