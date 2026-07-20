package com.redditclone.reddit_backend.dtos.Subreddit;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubredditRequest {

    @NotBlank(message = "Subreddit name is required")
    private String name;

    @NotBlank(message = "Subreddit description is required")
    private String description;

    @NotBlank(message = "Subreddit avatar URL is required")
    private String avatarUrl;

    @NotBlank(message = "Subreddit banner URL is required")
    private String bannerUrl;

    @NotBlank(message = "Subreddit privacy setting is required")
    private Boolean isPrivate;
}
