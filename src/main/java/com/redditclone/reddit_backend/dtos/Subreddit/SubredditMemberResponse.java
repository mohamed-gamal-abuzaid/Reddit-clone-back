package com.redditclone.reddit_backend.dtos.Subreddit;


import com.redditclone.reddit_backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubredditMemberResponse {

    private Long id;
    private String username;
    private SubredditResponse subreddit;
    private String role;

}
