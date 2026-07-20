package com.redditclone.reddit_backend.dtos.Subreddit;


import com.redditclone.reddit_backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubredditResponse {
    private Long id;
    private String name;
    private String description;
    private String avatarUrl;
    private String bannerUrl;
    private User creator;
    private Integer membersCount;
    private Integer postsCount;
    private LocalDateTime createdAt;
}
