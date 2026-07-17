package com.redditclone.reddit_backend.dtos.Vote;

import com.redditclone.reddit_backend.entity.VoteType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoteResponse {

    private Long id;

    private String username;

    private VoteType voteType;

    private Long postId;

    private Long commentId;
}
