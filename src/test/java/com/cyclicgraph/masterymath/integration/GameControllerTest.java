package com.cyclicgraph.masterymath.integration;

import com.cyclicgraph.masterymath.challenge.model.Challenge;
import com.cyclicgraph.masterymath.challenge.model.ChallengeStatistics;
import com.cyclicgraph.masterymath.challenge.repository.ChallengeRepository;
import com.cyclicgraph.masterymath.game.model.Game;
import com.cyclicgraph.masterymath.game.model.StartGameRequest;
import com.cyclicgraph.masterymath.game.repository.GameRepository;
import com.cyclicgraph.masterymath.util.WithMockUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerTest extends IntegrationTest {
    public static final String START_GAME_PATH = "/api/v1/game/start";
    public static final String GET_CHALLENGES_FOR_GAME_PATH = "/api/v1/game/challenges";

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private ChallengeRepository challengeRepository;

    private void createChallenges() {
        for (int i = 0; i < 10; i++) {
            Challenge entity = new Challenge();
            ChallengeStatistics statistics = new ChallengeStatistics();
            statistics.setRating(1000.0);
            entity.setStatistics(statistics);
            entity.setAnswer("1");
            challengeRepository.save(entity);
        }
    }

    @Test
    @WithMockUserEntity
    void shouldStartGame() throws Exception {
        createChallenges();

        mockMvc.perform(post(START_GAME_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(new StartGameRequest(true)))).andExpectAll(
                status().isOk(),
                jsonPath("$.gameId").isNotEmpty()
        );

        List<Game> all = gameRepository.findAll();
        Assertions.assertEquals(1, all.size());
        Assertions.assertEquals("user", all.get(0).getUser().getUsername());
    }

    @Test
    @WithMockUserEntity
    @DisplayName("should not start game because insufficient number of challenges in database")
    void shouldNotStartGame() throws Exception {
        mockMvc.perform(post(START_GAME_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(new StartGameRequest(true)))).andExpectAll(
                status().isBadRequest()
        );

        List<Game> all = gameRepository.findAll();
        Assertions.assertEquals(0, all.size());
    }

    @Test
    @WithMockUserEntity(roles = "ADMIN")
    @DisplayName("should not start game because user is admin")
    void shouldNotStartGameBecauseAdminRole() throws Exception {
        mockMvc.perform(post(START_GAME_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(new StartGameRequest(true)))).andExpectAll(
                status().isForbidden()
        );

        List<Game> all = gameRepository.findAll();
        Assertions.assertEquals(0, all.size());
    }

    @Test
    @DisplayName("should not start game because not authorized")
    void shouldNotStartGameBecauseUnauthorized() throws Exception {
        mockMvc.perform(post(START_GAME_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(new StartGameRequest(true)))).andExpectAll(
                status().isUnauthorized()
        );

        List<Game> all = gameRepository.findAll();
        Assertions.assertEquals(0, all.size());
    }

    @Test
    @WithMockUserEntity
    void shouldRetrieveChallenges() throws Exception {
        createChallenges();

        mockMvc.perform(post(START_GAME_PATH).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(new StartGameRequest(false))));

        UUID gameId = gameRepository.findAll().get(0).getId();
        mockMvc.perform(get(GET_CHALLENGES_FOR_GAME_PATH + "?gameId=" + gameId)).
                andExpectAll(
                        status().isOk(),
                        jsonPath("$.gameId").value(equalTo(gameId.toString())),
                        jsonPath("$.challenges", hasSize(10))
                );

    }
}
