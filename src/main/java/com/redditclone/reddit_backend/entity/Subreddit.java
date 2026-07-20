package com.redditclone.reddit_backend.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subreddits")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subreddit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "avatar_url")
    private String avatarUrl;


    @Column(name = "banner_url")
    private String bannerUrl;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "members_count")
    private Integer membersCount= 0;

    @Column(name = "posts_count")
    private Integer postsCount= 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "subreddit")
    private List<Post> posts;


    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate = false;

    @OneToMany(mappedBy = "subreddit")
    private List<SubredditMember> members;
}
