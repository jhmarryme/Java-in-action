package org.example.springsecurity6wtdemo.authcenter.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.springsecurity6wtdemo.authcenter.dao.request.SignUpRequest;
import org.example.springsecurity6wtdemo.authcenter.dao.request.SigninRequest;
import org.example.springsecurity6wtdemo.authcenter.dao.response.JwtAuthenticationResponse;
import org.example.springsecurity6wtdemo.authcenter.entities.Role;
import org.example.springsecurity6wtdemo.authcenter.entities.User;
import org.example.springsecurity6wtdemo.authcenter.repository.UserRepository;
import org.example.springsecurity6wtdemo.authcenter.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User.builder().firstName(request.getFirstName()).lastName(request.getLastName())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }
}
