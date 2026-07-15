package com.redditclone.reddit_backend.service;

import com.redditclone.reddit_backend.dtos.CreatePostRequest;
import com.redditclone.reddit_backend.dtos.PostResponse;
import com.redditclone.reddit_backend.entity.Post;
import com.redditclone.reddit_backend.entity.User;
import com.redditclone.reddit_backend.exception.ResourceNotFoundException;
import com.redditclone.reddit_backend.exception.UnauthorizedException;
import com.redditclone.reddit_backend.repository.PostRepository;
import com.redditclone.reddit_backend.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public PostResponse createPost(CreatePostRequest postRequest) {
        User currentUser = SecurityUtils.getCurrentUser();

        Post post = Post.builder()
                .author(currentUser)
                .title(postRequest.getTitle())
                .body(postRequest.getBody())
                .build();

        postRepository.save(post);

        return mapToResponse(post);
    }



    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        return mapToResponse(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, CreatePostRequest postRequest) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = SecurityUtils.getCurrentUser();
        if (!post.getAuthor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this post");
        }

        post.setTitle(postRequest.getTitle());
        post.setBody(postRequest.getBody());

        postRepository.save(post);

        return mapToResponse(post);
    }


    public List<PostResponse> listPosts() {
        List<Post> posts = postRepository.findAll();

        if (posts.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return posts
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public List<PostResponse> listPostsByUser(String username) {
        List<Post> posts = postRepository.findByAuthor_Username(username);

        if (posts.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        return posts
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User currentUser = SecurityUtils.getCurrentUser();
        if (!post.getAuthor().equals(currentUser)) {
            throw new UnauthorizedException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }




    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .author(post.getAuthor().getActualUsername())
                .voteScore(post.getVoteScore())
                .commentsCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .build();
    }


}
