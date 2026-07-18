package com.redditclone.reddit_backend.service;

import com.redditclone.reddit_backend.dtos.Vote.VoteRequest;
import com.redditclone.reddit_backend.dtos.Vote.VoteResponse;
import com.redditclone.reddit_backend.entity.*;
import com.redditclone.reddit_backend.exception.ResourceNotFoundException;
import com.redditclone.reddit_backend.repository.CommentRepository;
import com.redditclone.reddit_backend.repository.PostRepository;
import com.redditclone.reddit_backend.repository.VoteRepository;
import com.redditclone.reddit_backend.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;


    @Transactional
    public VoteResponse votePost(Long postId, VoteRequest request) {

        User currentUser = SecurityUtils.getCurrentUser();
        Post post = getPostOrThrow(postId, "Post not found");

        Vote vote = processPostVote(currentUser, post, request.getVoteType());

        return mapToVoteResponse(vote);
    }

    @Transactional
    public VoteResponse voteComment(Long commentId, VoteRequest request) {

        User currentUser = SecurityUtils.getCurrentUser();
        Comment comment = getCommentOrThrow(commentId, "Comment not found");

        Vote vote = processCommentVote(currentUser, comment, request.getVoteType());

        return mapToVoteResponse(vote);
    }


    public VoteResponse getVoteById(Long id) {

        Vote vote = getVoteOrThrow(id, "Vote not found");

        return mapToVoteResponse(vote);
    }


    public VoteResponse getCurrentUserVoteForPost(Long postId ){
        User currentUser = SecurityUtils.getCurrentUser();

        Post post = getPostOrThrow(postId, "Post not found");

        Vote vote = voteRepository.findByUserAndPost(currentUser, post)
                .orElse(null);


        return VoteResponse.builder()
                .voteType(vote != null ? vote.getVoteType() : null)
                .build();

    }


    public VoteResponse getCurrentUserVoteForComment(Long commentId) {

        User currentUser = SecurityUtils.getCurrentUser();

        Comment comment = getCommentOrThrow(commentId, "Comment not found");

        Vote vote = voteRepository.findByUserAndComment(currentUser, comment)
                .orElse(null);

        return VoteResponse.builder()
                .voteType(vote != null ? vote.getVoteType() : null)
                .build();
    }

    public void deleteVote(Long voteId) {
        Vote vote = getVoteOrThrow(voteId, "Vote not found");

        if (vote.getPost() != null) {
            updatePostScore(vote.getPost(), vote.getVoteType(), null);
        } else if (vote.getComment() != null) {
            updateCommentScore(vote.getComment(), vote.getVoteType(), null);
        }

        voteRepository.delete(vote);
    }


    private Vote processPostVote(User user, Post post, VoteType newVoteType) {

        Optional<Vote> existingVote =
                voteRepository.findByUserAndPost(user, post);

        if (existingVote.isPresent()) {

            Vote vote = existingVote.get();

            if (vote.getVoteType() == newVoteType) {

                updatePostScore(post, vote.getVoteType(), null);

                voteRepository.delete(vote);

                return vote;
            }

            updatePostScore(post, vote.getVoteType(), newVoteType);

            vote.setVoteType(newVoteType);

            return voteRepository.save(vote);
        }

        Vote vote = Vote.builder()
                .user(user)
                .post(post)
                .voteType(newVoteType)
                .build();

        updatePostScore(post, null, newVoteType);

        return voteRepository.save(vote);
    }



    private Vote processCommentVote(User user, Comment comment, VoteType newVoteType) {

        Optional<Vote> existingVote =
                voteRepository.findByUserAndComment(user, comment);

        if (existingVote.isPresent()) {

            Vote vote = existingVote.get();

            if (vote.getVoteType() == newVoteType) {

                updateCommentScore(comment, vote.getVoteType(), null);

                voteRepository.delete(vote);

                return vote;
            }

            updateCommentScore(comment, vote.getVoteType(), newVoteType);

            vote.setVoteType(newVoteType);

            return voteRepository.save(vote);
        }

        Vote vote = Vote.builder()
                .user(user)
                .comment(comment)
                .voteType(newVoteType)
                .build();

        updateCommentScore(comment, null, newVoteType);

        return voteRepository.save(vote);
    }

    private void updatePostScore(Post post, VoteType oldVote, VoteType newVote) {

        int score = post.getVoteScore();

        if (oldVote == null) {

            score += (newVote == VoteType.UPVOTE) ? 1 : -1;

        } else if (newVote == null) {

            score += (oldVote == VoteType.UPVOTE) ? -1 : 1;

        } else if (oldVote == VoteType.UPVOTE && newVote == VoteType.DOWNVOTE) {

            score -= 2;

        } else if (oldVote == VoteType.DOWNVOTE && newVote == VoteType.UPVOTE) {

            score += 2;
        }

        post.setVoteScore(score);
    }



    private void updateCommentScore(Comment comment, VoteType oldVote, VoteType newVote) {

        int score = comment.getVoteScore();

        if (oldVote == null) {

            score += (newVote == VoteType.UPVOTE) ? 1 : -1;

        } else if (newVote == null) {

            score += (oldVote == VoteType.UPVOTE) ? -1 : 1;

        } else if (oldVote == VoteType.UPVOTE && newVote == VoteType.DOWNVOTE) {

            score -= 2;

        } else if (oldVote == VoteType.DOWNVOTE && newVote == VoteType.UPVOTE) {

            score += 2;
        }

        comment.setVoteScore(score);
    }




    private VoteResponse mapToVoteResponse(Vote vote) {
        return VoteResponse.builder()
                .voteType(vote.getVoteType())
                .build();
    }


    private Vote getVoteOrThrow(Long id ,String message) {

        return voteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(message));

    }


    private Comment getCommentOrThrow(Long id ,String message) {

        return commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(message));

    }


    private Post getPostOrThrow(Long id ,String message) {

        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(message));

    }

}
