package com.redditclone.reddit_backend.repository;

import com.redditclone.reddit_backend.entity.Comment;
import com.redditclone.reddit_backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findByParent(Comment parent);
}
