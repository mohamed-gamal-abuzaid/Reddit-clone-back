package com.redditclone.reddit_backend.repository;

import com.redditclone.reddit_backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {


    List<Post> findByAuthor_Username(String username);
}
