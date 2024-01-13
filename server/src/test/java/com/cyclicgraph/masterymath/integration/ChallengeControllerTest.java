package com.cyclicgraph.masterymath.integration;

import com.cyclicgraph.masterymath.challenge.model.ChallengeMetadata;
import com.cyclicgraph.masterymath.challenge.model.CreateChallengeRequest;
import com.cyclicgraph.masterymath.challenge.repository.ChallengeRepository;
import com.cyclicgraph.masterymath.util.WithMockUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChallengeControllerTest extends IntegrationTest {
    private static final String CREATE_CHALLENGE_PATH = "/api/v1/challenge";

    @Autowired
    private ChallengeRepository challengeRepository;

    private static Stream<CreateChallengeRequest> correctCreateChallengeRequests() {
        return Stream.of(
                // no correctness validation yet
                new CreateChallengeRequest("2+2=", "5", 400, ChallengeMetadata.ChallengeType.BASIC_OPERATION),
                new CreateChallengeRequest("15*(2+2)=", "60", 800, ChallengeMetadata.ChallengeType.COMPOSITE_OPERATION),
                new CreateChallengeRequest("\\int_{0}^{3}x^2dx=", "9", 1200, ChallengeMetadata.ChallengeType.INTEGRAL),
                new CreateChallengeRequest("\\sqrt[3]{27}=", "3", 1000, ChallengeMetadata.ChallengeType.NTH_ROOT)
        );
    }

    private static Stream<CreateChallengeRequest> incorrectCreateChallengeRequests() {
        return Stream.of(
                new CreateChallengeRequest("", "5", 400, ChallengeMetadata.ChallengeType.BASIC_OPERATION),
                new CreateChallengeRequest(null, "5", 400, ChallengeMetadata.ChallengeType.BASIC_OPERATION),
                new CreateChallengeRequest("2+2", "5", 400, ChallengeMetadata.ChallengeType.BASIC_OPERATION),
                new CreateChallengeRequest("2+2=", "", 400, ChallengeMetadata.ChallengeType.BASIC_OPERATION),
                new CreateChallengeRequest("2+2=", null, 400, ChallengeMetadata.ChallengeType.BASIC_OPERATION),
                new CreateChallengeRequest("2+2=", "5", 0, ChallengeMetadata.ChallengeType.BASIC_OPERATION),
                new CreateChallengeRequest("2+2=", "5", -55.2, ChallengeMetadata.ChallengeType.BASIC_OPERATION),
                new CreateChallengeRequest("2+2=", "5", 400, null),
                new CreateChallengeRequest("\\invalidCommand", "5", 400, ChallengeMetadata.ChallengeType.BASIC_OPERATION)
        );
    }

    @ParameterizedTest
    @MethodSource("correctCreateChallengeRequests")
    @WithMockUserEntity(username = "admin", roles = {"ADMIN"})
    void shouldAddChallenge(CreateChallengeRequest request) throws Exception {
        mockMvc.perform(post(CREATE_CHALLENGE_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(request))).andExpectAll(
                status().isOk()
        );

        Assertions.assertEquals(1, challengeRepository.findAll().size());
    }

    @ParameterizedTest
    @MethodSource("incorrectCreateChallengeRequests")
    @WithMockUserEntity(username = "admin", roles = {"ADMIN"})
    void shouldNotAddChallenge(CreateChallengeRequest request) throws Exception {
        mockMvc.perform(post(CREATE_CHALLENGE_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(request))).andExpectAll(
                status().isBadRequest()
        );

        Assertions.assertEquals(0, challengeRepository.findAll().size());
    }

    @Test
    @WithMockUserEntity
    void shouldNotAddChallengeBecauseInsufficientRole() throws Exception {
        mockMvc.perform(post(CREATE_CHALLENGE_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(new CreateChallengeRequest("2+2=", "4", 1000, ChallengeMetadata.ChallengeType.BASIC_OPERATION)))).andExpectAll(
                status().isForbidden()
        );

    }
}
