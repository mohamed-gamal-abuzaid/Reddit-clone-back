package com.redditclone.reddit_backend.dtos.Comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotBlank
    private String body;
    private Long parentId;
}
