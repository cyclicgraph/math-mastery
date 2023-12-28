package com.cyclicgraph.masterymath.auth;

import com.cyclicgraph.masterymath.auth.model.Jwt;
import com.cyclicgraph.masterymath.auth.model.RefreshToken;
import com.cyclicgraph.masterymath.auth.model.RefreshTokenRequest;
import com.cyclicgraph.masterymath.auth.model.SignInRequest;
import com.cyclicgraph.masterymath.auth.model.SignUpRequest;
import com.cyclicgraph.masterymath.auth.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping(value = "/signup")
    public ResponseEntity<Jwt> signup(HttpServletResponse response, @RequestBody @Valid SignUpRequest request) {
        Pair<Jwt, RefreshToken> signup = authenticationService.signup(request);
        addRefreshTookenCookie(response, signup.getRight());

        return ResponseEntity.ok(signup.getLeft());
    }

    @PostMapping(value = "/signin")
    public ResponseEntity<Jwt> signup(HttpServletResponse response, @RequestBody @Valid SignInRequest request) {
        Pair<Jwt, RefreshToken> signin = authenticationService.signin(request);
        addRefreshTookenCookie(response, signin.getRight());

        return ResponseEntity.ok(signin.getLeft());
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<Jwt> refresh(HttpServletResponse response, @RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationService.refresh(request));
    }


    private void addRefreshTookenCookie(HttpServletResponse response, RefreshToken refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", String.valueOf(refreshToken.getToken()));
        // not very accurate...
        refreshTokenCookie.setMaxAge((int) ((refreshToken.getExpires() - new Date().getTime()) / 1000));
        response.addCookie(refreshTokenCookie);
    }

}
