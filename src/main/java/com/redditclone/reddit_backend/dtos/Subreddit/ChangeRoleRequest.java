package com.redditclone.reddit_backend.dtos.Subreddit;

import com.redditclone.reddit_backend.entity.SubredditRoles;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeRoleRequest {
    @NotBlank(message = "Role is required")
    private SubredditRoles role;
}