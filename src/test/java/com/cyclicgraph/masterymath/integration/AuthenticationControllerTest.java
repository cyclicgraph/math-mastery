package com.cyclicgraph.masterymath.integration;

import com.cyclicgraph.masterymath.auth.model.RefreshToken;
import com.cyclicgraph.masterymath.auth.model.RefreshTokenRequest;
import com.cyclicgraph.masterymath.auth.model.SignInRequest;
import com.cyclicgraph.masterymath.auth.model.SignUpRequest;
import com.cyclicgraph.masterymath.auth.repository.RefreshTokenRepository;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import com.cyclicgraph.masterymath.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AuthenticationControllerTest extends IntegrationTest {
    public static final String SIGNUP_PATH = "/api/v1/auth/signup";
    public static final String SIGNIN_PATH = "/api/v1/auth/signin";
    public static final String REFRESH_PATH = "/api/v1/auth/refresh";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private static Stream<SignUpRequest> correctSignupRequests() {
        return Stream.of(new SignUpRequest("test", "test@test.com", "test"), new SignUpRequest("test129iv34", "test1324@124test.com", "test13254"));
    }

    private static Stream<SignUpRequest> incorrectSignupRequests() {
        return Stream.of(
                new SignUpRequest("", "test@test.com", "test"),
                new SignUpRequest(null, "test@test.com", "test"),
                new SignUpRequest("test129iv34", "", "test13254"),
                new SignUpRequest("test129iv34", null, "test13254"),
                new SignUpRequest("test129iv34", "test", "test13254"),
                new SignUpRequest("test", "test@test.com", ""),
                new SignUpRequest("test", "test@test.com", null)
        );
    }

    @ParameterizedTest
    @MethodSource("correctSignupRequests")
    void shouldSignup(SignUpRequest signUpRequest) throws Exception {
        mockMvc.perform(post(SIGNUP_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(signUpRequest))).andExpectAll(
                status().isOk(),
                jsonPath("$.token").isNotEmpty(),
                jsonPath("$.expiration").isNotEmpty(),
                cookie().exists("refresh_token")
        );

        Assertions.assertEquals(1, userRepository.findAll().size());
        Assertions.assertEquals(1, refreshTokenRepository.findAll().size());
    }

    @ParameterizedTest
    @MethodSource("incorrectSignupRequests")
    void shouldNotSignup(SignUpRequest signUpRequest) throws Exception {
        mockMvc.perform(post(SIGNUP_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(signUpRequest))).andExpectAll(
                status().isBadRequest(),
                cookie().doesNotExist("refresh_token")
        );

        Assertions.assertEquals(0, userRepository.findAll().size());
        Assertions.assertEquals(0, refreshTokenRepository.findAll().size());
    }

    @Test
    void shouldSignIn() throws Exception {
        UserEntity existingUser = testUtils.createUserWithRefreshToken().getKey();

        mockMvc.perform(post(SIGNIN_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(new SignInRequest(existingUser.getUsername(), "password")))).andExpectAll(
                status().isOk(),
                jsonPath("$.token").isNotEmpty(),
                jsonPath("$.expiration").isNotEmpty(),
                cookie().exists("refresh_token")
        );
    }

    @Test
    void shouldNotSignIn() throws Exception {
        UserEntity existingUser = testUtils.createUserWithRefreshToken().getKey();

        mockMvc.perform(post(SIGNIN_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(new SignInRequest(existingUser.getUsername(), "wrong")))).andExpectAll(
                status().isBadRequest()
        );
    }

    @Test
    void shouldRefreshToken() throws Exception {
        RefreshToken refreshToken = testUtils.createUserWithRefreshToken().getValue();

        mockMvc.perform(post(REFRESH_PATH).contentType(MediaType.APPLICATION_JSON).content(
                        objectMapper.writeValueAsString(new RefreshTokenRequest(refreshToken.getToken().toString()))))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.token").isNotEmpty(),
                        jsonPath("$.expiration").isNotEmpty(),
                        cookie().doesNotExist("refresh_token")
                );
    }

    @Test
    void shouldNotRefreshTokenBecauseNotExistingToken() throws Exception {
        mockMvc.perform(post(REFRESH_PATH).contentType(MediaType.APPLICATION_JSON).content(
                        objectMapper.writeValueAsString(new RefreshTokenRequest(UUID.randomUUID().toString()))))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.token").doesNotExist(),
                        jsonPath("$.expiration").doesNotExist(),
                        cookie().doesNotExist("refresh_token")
                );
    }

    @Test
    void shouldNotRefreshTokenBecauseExpired() throws Exception {
        RefreshToken refreshToken = testUtils.createUserWithRefreshToken().getValue();
        refreshToken.setExpires(new Date().getTime());

        refreshTokenRepository.save(refreshToken);

        mockMvc.perform(post(REFRESH_PATH).contentType(MediaType.APPLICATION_JSON).content(
                        objectMapper.writeValueAsString(new RefreshTokenRequest(UUID.randomUUID().toString()))))
                .andExpectAll(
                        status().is4xxClientError(),
                        jsonPath("$.token").doesNotExist(),
                        jsonPath("$.expiration").doesNotExist(),
                        cookie().doesNotExist("refresh_token")
                );

    }

}
