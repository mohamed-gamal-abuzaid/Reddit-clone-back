package com.redditclone.reddit_backend.security;

import com.redditclone.reddit_backend.entity.Role;
import com.redditclone.reddit_backend.entity.User;
import com.redditclone.reddit_backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User =
                (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {

                    User newUser = User.builder()
                            .email(email)
                            .username(email.split("@")[0])
                            .displayName(name)
                            .avatarUrl(picture)
                            .password("")
                            .role(Role.USER)
                            .build();

                    return userRepository.save(newUser);
                });

        String accessToken =
                jwtService.generateAccessToken(user);

        response.sendRedirect(
                "http://localhost:3000/auth/callback?accessToken="
                        + accessToken
        );
    }
}