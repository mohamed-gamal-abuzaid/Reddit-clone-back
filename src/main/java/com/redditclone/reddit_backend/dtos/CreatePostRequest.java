package com.redditclone.reddit_backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String body;
}
