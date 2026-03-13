package com.yunus.service;

import com.yunus.dto.AuthRequest;
import com.yunus.dto.AuthResponse;
import com.yunus.exception.BaseException;
import com.yunus.exception.ErrorType;
import com.yunus.model.Role;
import com.yunus.model.User;
import com.yunus.repository.RoleRepository;
import com.yunus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(AuthRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BaseException(ErrorType.DUPLICATE_ENTRY, "Bu Kullanıcı Adı Zaten Mevcut");
        }

        Role userRole = roleRepository.findByName(Role.RoleName.USER.name()).
                orElseThrow(() -> new BaseException(ErrorType.NOT_FOUND, "USER ROLE NOT FOUND"));


        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);


        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .message("Kayıt Başarılı...")
                .build();
    }


    public AuthResponse login(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getUsername());

        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .username(request.getUsername())
                .message("Giriş Başarılı")
                .build();
    }


}
