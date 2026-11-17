package com.sunasterisk.miniproject.service;

import com.sunasterisk.miniproject.dto.AuthRequest;
import com.sunasterisk.miniproject.dto.AuthResponse;
import com.sunasterisk.miniproject.dto.RegisterRequest;
import com.sunasterisk.miniproject.entity.User;
import com.sunasterisk.miniproject.entity.User.Role;
import com.sunasterisk.miniproject.exception.DuplicateResourceException;
import com.sunasterisk.miniproject.repository.UserRepository;
import com.sunasterisk.miniproject.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }
        Role role = request.getRole() != null ? request.getRole() : Role.USER;
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                role);
        return userRepository.save(user);
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = jwtTokenProvider.generateToken(authentication);
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(Object::toString)
                .orElse("ROLE_USER");
        return new AuthResponse(token, request.getUsername(), role);
    }
}
