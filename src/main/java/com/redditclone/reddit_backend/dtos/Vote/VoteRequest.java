package com.redditclone.reddit_backend.dtos.Vote;

import com.redditclone.reddit_backend.entity.VoteType;
import lombok.Data;

@Data
public class VoteRequest {

    private VoteType voteType;
}
