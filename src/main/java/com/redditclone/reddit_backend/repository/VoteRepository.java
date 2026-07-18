package com.redditclone.reddit_backend.repository;

import com.redditclone.reddit_backend.entity.Comment;
import com.redditclone.reddit_backend.entity.Post;
import com.redditclone.reddit_backend.entity.User;
import com.redditclone.reddit_backend.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndPost(User currentUser, Post post);

    Optional<Vote> findByUserAndComment(User currentUser, Comment comment);
}
