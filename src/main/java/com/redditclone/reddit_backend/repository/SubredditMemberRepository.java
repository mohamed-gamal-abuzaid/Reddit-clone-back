package com.redditclone.reddit_backend.repository;

import com.redditclone.reddit_backend.entity.Subreddit;
import com.redditclone.reddit_backend.entity.SubredditMember;
import com.redditclone.reddit_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubredditMemberRepository extends JpaRepository<SubredditMember, Long> {
    SubredditMember findByUserAndSubreddit(User currentUser, Subreddit subreddit);

    List<SubredditMember> findBySubreddit(Subreddit subreddit);
}
