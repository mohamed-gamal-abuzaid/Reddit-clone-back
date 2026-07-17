package com.redditclone.reddit_backend.dtos.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String body;
    private String author;
    private Integer voteScore;
    private Integer commentsCount;
    private LocalDateTime createdAt;

}
