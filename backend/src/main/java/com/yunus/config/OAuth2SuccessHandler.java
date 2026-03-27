package com.yunus.config;

import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.model.Role;
import com.yunus.model.User;
import com.yunus.repository.RoleRepository;
import com.yunus.repository.UserRepository;
import com.yunus.service.CustomUserDetailsService;
import com.yunus.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUrl;


    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        userRepository.findByUsername(email).orElseGet(() -> {

            Role userRole = roleRepository.findByName(Role.RoleName.USER)
                    .orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, "User role not found"));


            return userRepository.save(User.builder()
                    .username(email).password(" ").roles(Set.of(userRole)).build());

        });

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        String token = jwtService.generateToken(userDetails);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl + "?token=" + token);

    }

}
