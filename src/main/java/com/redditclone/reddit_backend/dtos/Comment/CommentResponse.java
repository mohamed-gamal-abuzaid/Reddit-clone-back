package com.redditclone.reddit_backend.dtos.Comment;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Long id;

    private String body;

    private String author;

    private Long postId;

    private Long parentCommentId;

    private Integer voteScore;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
