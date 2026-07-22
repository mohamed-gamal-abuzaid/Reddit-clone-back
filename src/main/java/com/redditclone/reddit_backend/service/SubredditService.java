package com.redditclone.reddit_backend.service;


import com.redditclone.reddit_backend.dtos.Subreddit.ChangeRoleRequest;
import com.redditclone.reddit_backend.dtos.Subreddit.SubredditMemberResponse;
import com.redditclone.reddit_backend.dtos.Subreddit.SubredditRequest;
import com.redditclone.reddit_backend.dtos.Subreddit.SubredditResponse;
import com.redditclone.reddit_backend.entity.Subreddit;
import com.redditclone.reddit_backend.entity.SubredditMember;
import com.redditclone.reddit_backend.entity.SubredditRoles;
import com.redditclone.reddit_backend.entity.User;
import com.redditclone.reddit_backend.exception.BadRequestException;
import com.redditclone.reddit_backend.exception.ForbiddenException;
import com.redditclone.reddit_backend.exception.ResourceNotFoundException;
import com.redditclone.reddit_backend.repository.SubredditMemberRepository;
import com.redditclone.reddit_backend.repository.SubredditRepository;
import com.redditclone.reddit_backend.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubredditService {

    private final SubredditRepository subredditRepository;

    private final SubredditMemberRepository subredditMemberRepository;


    @Transactional
    public SubredditResponse createSubreddit(SubredditRequest subredditRequest) {

        User currentUser = getCurrentUser();

        Subreddit subreddit = Subreddit.builder()
                .name(subredditRequest.getName())
                .description(subredditRequest.getDescription())
                .avatarUrl(subredditRequest.getAvatarUrl())
                .bannerUrl(subredditRequest.getBannerUrl())
                .creator(currentUser)
                .membersCount(1)
                .isPrivate(subredditRequest.getIsPrivate())
                .build();

        subredditRepository.save(subreddit);

        SubredditMember member =SubredditMember.builder()
                .user(currentUser)
                .subreddit(subreddit)
                .role(com.redditclone.reddit_backend.entity.SubredditRoles.OWNER)
                .build();


        subredditMemberRepository.save(member);

        return mapToResponse(subreddit);
    }


    @Transactional
    public SubredditResponse updateSubreddit(Long subredditId, SubredditRequest subredditRequest) {
        User currentUser = getCurrentUser();
        Subreddit subreddit = getSubredditOrThrow(subredditId);

        SubredditMember member = getCurrentMember(currentUser, subreddit);

        if (member == null) {
            throw new ResourceNotFoundException("Member not found");
        }

        if(member.getRole() != com.redditclone.reddit_backend.entity.SubredditRoles.OWNER) {
            throw new ForbiddenException("You are not authorized to update this subreddit");
        }


        subreddit.setDescription(subredditRequest.getDescription());
        subreddit.setAvatarUrl(subredditRequest.getAvatarUrl());
        subreddit.setBannerUrl(subredditRequest.getBannerUrl());

        subredditRepository.save(subreddit);

        return mapToResponse(subreddit);
    }


    public SubredditResponse getSubredditById(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId).orElseThrow(() -> new ResourceNotFoundException("Subreddit not found"));

        return mapToResponse(subreddit);
    }



    public List<SubredditResponse> getAllSubreddits() {
        List<Subreddit> subreddits = subredditRepository.findAll();

        return subreddits
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Transactional
    public void deleteSubreddit(Long subredditId) {

        User currentUser = getCurrentUser();
        Subreddit subreddit = getSubredditOrThrow(subredditId);

        SubredditMember member = getCurrentMember(currentUser, subreddit);

        if (member == null) {
            throw new ResourceNotFoundException("Member not found");
        }

        if(member.getRole() != com.redditclone.reddit_backend.entity.SubredditRoles.OWNER) {
            throw new ForbiddenException("You are not authorized to Delete this subreddit");
        }

        subredditRepository.delete(subreddit);


    }


    public void  joinSubreddit(long subredditId) {
        User currentUser = getCurrentUser();
        Subreddit subreddit = getSubredditOrThrow(subredditId);

        SubredditMember member = getCurrentMember(currentUser, subreddit);

        if (member != null) {
            throw new BadRequestException("You are already a member of this subreddit");
        }

        member = SubredditMember.builder()
                .user(currentUser)
                .subreddit(subreddit)
                .role(com.redditclone.reddit_backend.entity.SubredditRoles.MEMBER)
                .build();

        subreddit.setMembersCount(
                subreddit.getMembersCount() + 1
        );
        subredditMemberRepository.save(member);

        subredditRepository.save(subreddit);
    }


    public void leaveSubreddit(long subredditId) {
        User currentUser = getCurrentUser();
        Subreddit subreddit = getSubredditOrThrow(subredditId);
        SubredditMember member = getCurrentMember(currentUser, subreddit);

        if (member == null) {
            throw new BadRequestException("You are not a member of this subreddit");
        }

        member.setLeavedAt(LocalDateTime.now());

        subreddit.setMembersCount(
                subreddit.getMembersCount() - 1
        );

        subredditMemberRepository.save(member);
        subredditRepository.save(subreddit);
    }



    public List<SubredditMemberResponse> getMembers(Long subredditId) {
        Subreddit subreddit = getSubredditOrThrow(subredditId);
        List<SubredditMember> members = subredditMemberRepository.findBySubredditAndLeavedAtIsNull(subreddit);
        return members.stream()
                .map(this::mapToMemberResponse)
                .toList();
    }


    @Transactional
    public SubredditMemberResponse changeMemberRole(Long subredditId, Long memberId, ChangeRoleRequest request) {

        User currentUser = getCurrentUser();
        Subreddit subreddit = getSubredditOrThrow(subredditId);

        SubredditMember owner = getCurrentMember(currentUser, subreddit);

        if (owner == null) {
            throw new ResourceNotFoundException("You are not a member of this subreddit");
        }

        if (owner.getRole() != SubredditRoles.OWNER) {
            throw new ForbiddenException("Only the owner can change member roles");
        }

        SubredditMember targetMember = subredditMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));


        if (!targetMember.getSubreddit().getId().equals(subredditId)) {
            throw new BadRequestException("This member does not belong to this subreddit");
        }

        if (targetMember.getRole() == SubredditRoles.OWNER) {
            throw new BadRequestException("Owner role cannot be changed");
        }

        if (targetMember.getRole() == request.getRole()) {
            throw new BadRequestException("Member already has this role");
        }

        targetMember.setRole(request.getRole());

        subredditMemberRepository.save(targetMember);

        return mapToMemberResponse(targetMember);
    }


    @Transactional
    public void removeMember(Long subredditId, Long memberId ){
        User currentUser = getCurrentUser();
        Subreddit subreddit = getSubredditOrThrow(subredditId);

        SubredditMember owner = getCurrentMember(currentUser, subreddit);

        SubredditMember targetMember =
                subredditMemberRepository.findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (owner == null) {
            throw new ResourceNotFoundException("You are not a member of this subreddit");
        }

        if (owner.getRole() != SubredditRoles.OWNER) {
            throw new ForbiddenException("Only the owner can remove members");
        }

        if (!targetMember.getSubreddit().getId().equals(subredditId)) {
            throw new BadRequestException("This member does not belong to this subreddit");
        }

        targetMember.setLeavedAt(LocalDateTime.now());
        subreddit.setMembersCount(subreddit.getMembersCount() - 1);
        subredditMemberRepository.save(targetMember);
    }



    public List<SubredditResponse> searchSubreddits(String query) {

        List<Subreddit> subreddits = subredditRepository.findByNameContainingIgnoreCase(query);

        return subreddits.stream()
                .map(this::mapToResponse)
                .toList();
    }



    private User getCurrentUser() {
        return SecurityUtils.getCurrentUser();
    }

    private Subreddit getSubredditOrThrow(Long subredditId) {
        return subredditRepository.findById(subredditId)
                .orElseThrow(() -> new ResourceNotFoundException("Subreddit not found"));
    }

    private SubredditMember getCurrentMember(User user, Subreddit subreddit) {
        return subredditMemberRepository.findByUserAndSubredditAndLeavedAtIsNull(user, subreddit);
    }



    private SubredditMemberResponse mapToMemberResponse(SubredditMember subredditMember) {
        return SubredditMemberResponse.builder()
                .id(subredditMember.getId())
                .username(subredditMember.getUser().getUsername())
                .subreddit(mapToResponse(subredditMember.getSubreddit()))
                .role(subredditMember.getRole().name())
                .build();
    }

    public SubredditResponse mapToResponse(Subreddit subreddit) {
        return SubredditResponse.builder()
                .id(subreddit.getId())
                .name(subreddit.getName())
                .description(subreddit.getDescription())
                .avatarUrl(subreddit.getAvatarUrl())
                .bannerUrl(subreddit.getBannerUrl())
                .creator(subreddit.getCreator())
                .membersCount(subreddit.getMembersCount())
                .postsCount(subreddit.getPostsCount())
                .createdAt(subreddit.getCreatedAt())
                .build();
    }


}
