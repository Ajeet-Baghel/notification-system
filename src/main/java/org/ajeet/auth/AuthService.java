package org.ajeet.auth;

import org.ajeet.auth.dto.AuthResponse;
import org.ajeet.auth.dto.LoginRequest;
import org.ajeet.auth.dto.RegisterRequest;
import org.ajeet.notification.event.UserLoggedInEvent;
import org.ajeet.notification.event.UserRegisteredEvent;
import org.ajeet.security.JwtService;
import org.ajeet.user.User;
import org.ajeet.user.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApplicationEventPublisher events;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       ApplicationEventPublisher events) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.events = events;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new IllegalArgumentException("Phone number is already registered");
        }

        User user = userRepository.save(new User(
                request.name(),
                request.email(),
                request.phone(),
                passwordEncoder.encode(request.password())));

        events.publishEvent(new UserRegisteredEvent(user.getName(), user.getEmail(), user.getPhone()));

        return new AuthResponse(jwtService.generateToken(user.getEmail()), user.getName(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        events.publishEvent(new UserLoggedInEvent(user.getName(), user.getEmail(), user.getPhone(), Instant.now()));

        return new AuthResponse(jwtService.generateToken(user.getEmail()), user.getName(), user.getEmail());
    }
}
