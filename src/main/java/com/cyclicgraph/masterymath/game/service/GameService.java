package com.cyclicgraph.masterymath.game.service;

import com.cyclicgraph.masterymath.challenge.model.Challenge;
import com.cyclicgraph.masterymath.challenge.service.ChallengeService;
import com.cyclicgraph.masterymath.game.model.Game;
import com.cyclicgraph.masterymath.game.repository.GameRepository;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import com.cyclicgraph.masterymath.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameService {
    public static final int CHALLENGES_IN_GAME = 10;
    private final GameRepository gameRepository;
    private final UserService userService;
    private final ChallengeService challengeService;

    public UUID start(boolean rated) {
        UUID uuid = UUID.randomUUID();
        UserEntity user = userService.getCurrent();
        List<Challenge> challenges = challengeService.drawRandom(CHALLENGES_IN_GAME, user.getChallengeRating());
        if (challenges.size() < CHALLENGES_IN_GAME) throw new IllegalStateException("not enough challenges drawn, expected " + CHALLENGES_IN_GAME + ", but was " + challenges.size());
        Game game = new Game(uuid, challenges, rated, user);

        return gameRepository.save(game).getId();
    }

    public Collection<Challenge> getChallenges(UUID gameId) {
        if (gameId == null) throw new IllegalArgumentException("gameId is null");
        Optional<Game> game = gameRepository.findById(gameId);
        if (game.isEmpty()) throw new IllegalArgumentException("no game with uuid " + gameId);

        // no validation if its users game

        return game.get().getChallenges().keySet();
    }
}
