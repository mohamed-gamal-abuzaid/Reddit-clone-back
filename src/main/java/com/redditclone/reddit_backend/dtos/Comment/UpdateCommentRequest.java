package com.redditclone.reddit_backend.dtos.Comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentRequest {


    @NotBlank
    private String body;
}
