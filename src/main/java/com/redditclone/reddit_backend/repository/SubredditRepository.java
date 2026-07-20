package com.redditclone.reddit_backend.repository;

import com.redditclone.reddit_backend.entity.Subreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubredditRepository extends JpaRepository<Subreddit, Long> {
    List<Subreddit> findByNameContainingIgnoreCase(String query);
}
