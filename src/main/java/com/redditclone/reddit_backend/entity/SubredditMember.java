package com.redditclone.reddit_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subreddit_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubredditMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subreddit_id", nullable = false)
    private Subreddit subreddit;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubredditRoles role;


    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;


    @Column(name = "leaved_at")
    private LocalDateTime leavedAt;


    private Boolean muted = false;

    private Boolean banned = false;
}
