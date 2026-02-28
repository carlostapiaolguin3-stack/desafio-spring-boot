package com.previred.desafio.service;

import com.previred.desafio.dto.AuthRequest;
import com.previred.desafio.dto.AuthResponse;
import com.previred.desafio.exception.ResourceNotFoundException;
import com.previred.desafio.model.User;
import com.previred.desafio.repository.UserRepository;
import com.previred.desafio.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + request.username()));

        log.info("Usuario '{}' autenticado exitosamente", request.username());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }
}
