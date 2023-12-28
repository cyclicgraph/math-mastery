package com.cyclicgraph.masterymath.auth.service;

import com.cyclicgraph.masterymath.auth.model.Jwt;
import com.cyclicgraph.masterymath.auth.model.RefreshToken;
import com.cyclicgraph.masterymath.auth.model.RefreshTokenRequest;
import com.cyclicgraph.masterymath.auth.model.SignInRequest;
import com.cyclicgraph.masterymath.auth.model.SignUpRequest;
import com.cyclicgraph.masterymath.auth.repository.RefreshTokenRepository;
import com.cyclicgraph.masterymath.config.security.JwtService;
import com.cyclicgraph.masterymath.role.RoleRepository;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import com.cyclicgraph.masterymath.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationProvider authenticationProvider;
    private final RoleRepository roleRepository;

    public Pair<Jwt, RefreshToken> signup(SignUpRequest request) {
        // role should be present due to logic around application startup
        UserEntity user = new UserEntity(UUID.randomUUID(), request.username(), passwordEncoder.encode(request.password()), request.email(), Collections.singletonList(roleRepository.findByName("ROLE_USER").get()));

        userRepository.save(user);
        Jwt jwt = jwtService.generateAccessToken(user.getUsername());
        RefreshToken refreshToken = refreshTokenRepository.save(jwtService.createRefreshToken(user));

        return Pair.of(jwt, refreshToken);
    }

    public Pair<Jwt, RefreshToken> signin(SignInRequest request) {
        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("wrong credentials provided");
        }

        Jwt jwt = jwtService.generateAccessToken(request.username());
        RefreshToken refreshToken = refreshTokenRepository.save(jwtService.createRefreshToken((UserEntity) authentication.getPrincipal()));

        return Pair.of(jwt, refreshToken);
    }


    public Jwt refresh(RefreshTokenRequest request) {
        Optional<RefreshToken> token = refreshTokenRepository.findByToken(UUID.fromString(request.refreshToken()));
        if (token.isEmpty()) {
            throw new IllegalArgumentException("refresh token %s not present".formatted(request.refreshToken()));
        }

        if (token.get().getExpires() < new Date().getTime()) {
            throw new IllegalArgumentException("refresh token %s has expired".formatted(request.refreshToken()));
        }

        return jwtService.generateAccessToken(token.get().getOwner().getUsername());
    }
}
