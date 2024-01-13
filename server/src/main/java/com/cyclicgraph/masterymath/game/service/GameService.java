package com.cyclicgraph.masterymath.game.service;

import com.cyclicgraph.masterymath.challenge.model.Challenge;
import com.cyclicgraph.masterymath.challenge.model.ChallengeGainsSnapshot;
import com.cyclicgraph.masterymath.challenge.model.ChallengeState;
import com.cyclicgraph.masterymath.challenge.model.RatingGain;
import com.cyclicgraph.masterymath.challenge.service.ChallengeService;
import com.cyclicgraph.masterymath.game.model.AnswerRequestEntry;
import com.cyclicgraph.masterymath.game.model.AnswerResponse;
import com.cyclicgraph.masterymath.game.model.AnswerResponseEntry;
import com.cyclicgraph.masterymath.game.model.ChallengeSubmission;
import com.cyclicgraph.masterymath.game.model.Game;
import com.cyclicgraph.masterymath.game.repository.GameRepository;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import com.cyclicgraph.masterymath.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    public static final int CHALLENGES_IN_GAME = 5;
    private final GameRepository gameRepository;
    private final UserService userService;
    private final ChallengeService challengeService;

    public UUID start(boolean rated) {
        UUID uuid = UUID.randomUUID();
        UserEntity user = userService.getCurrent();
        List<Challenge> challenges = challengeService.drawRandom(CHALLENGES_IN_GAME, user.getChallengeRating());

        Game game = new Game(uuid, transform(challenges, user), rated, user, false);

        Game save = gameRepository.save(game);
        return save.getId();
    }

    /**
     * Get challenges for game with given id
     *
     * @param gameId id of game
     * @return list of challenges for game
     * @throws IllegalArgumentException if gameId is null or no game with given id
     */
    public Collection<ChallengeSubmission> getChallenges(UUID gameId) throws IllegalArgumentException {
        return findGame(gameId).getChallenges();
    }

    private Game findGame(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("no game with uuid " + gameId));
    }

    private Set<ChallengeSubmission> transform(List<Challenge> challenges, UserEntity user) {
        return challenges.stream().map(challenge -> {
                    var ratingToGain = new RatingCalculator(user.getChallengeRating(), user.getPeakRating()).calculateGain(challenge.getStatistics().getRating());

                    // to prevent challenge changes during game and consequent rating fluctuations, we calculate gains on start
                    List<RatingGain> gains = getGains(ratingToGain, challenge.getStatistics().getExpectedSeconds());
                    return new ChallengeSubmission(
                            new ChallengeGainsSnapshot(challenge.getId(), gains),
                            ChallengeState.NONE
                    );
                }
        ).collect(Collectors.toSet());
    }

    /**
     * Processes the answers submitted for a game and returns the aggregated response.
     *
     * @param gameId  the ID of the game
     * @param answers a list of answer request entries containing the challenge ID, the hashed answer, and the seconds taken to answer
     * @return the response containing the game ID, the entries with information about each challenge, and the total gain
     */
    @Transactional
    public AnswerResponse answer(UUID gameId, List<AnswerRequestEntry> answers) {
        // probably not the thread-safe solution, further tests are required

        AtomicReference<Double> totalGain = new AtomicReference<>(0.0);
        Game game = findGame(gameId);
        if (game.isDone()) throw new IllegalArgumentException("game is already done");
        var challenges = challengeService.findAllById(game.getChallenges().stream().map(submission -> submission.getSnapshot().id()).collect(Collectors.toList()));

        var entries = game.getChallenges().stream().map(submission -> {
            var c = submission.getSnapshot();
            // for every challenge, find the appropriate AnswerRequestEntry by challengeId
            var entry = answers.stream().filter(a -> a.challengeId() == c.id()).findFirst().orElseThrow(
                    () -> new IllegalArgumentException("no hashedAnswer for challenge " + c.id())
            );

            Challenge challenge = challenges.stream().filter(ch -> ch.getId().equals(c.id())).findFirst().orElseThrow(() -> new IllegalArgumentException("no challenge with id " + c.id()));

            // check if answer is correct, despite expecting correct answer - validation is done on frontend
            boolean isCorrect = entry.answer().equals(challenge.getAnswer());
            submission.setState(isCorrect ? ChallengeState.CORRECT : ChallengeState.WRONG);

            // calculate gain for each of hashedAnswer
            var ratingGain = calculateGain(c.gains(), entry.seconds(), isCorrect);
            totalGain.updateAndGet(v -> v + ratingGain);

            // update challenge rating
            challenge.updateAfterAnswer(isCorrect, isCorrect ? -ratingGain : ratingGain);

            return new AnswerResponseEntry(c.id(), isCorrect, ratingGain);
        }).toList();
        game.setDone(true);
        game.getUser().updateRating(totalGain.get());

        return new AnswerResponse(gameId, entries, totalGain.get());
    }

    public double calculateGain(List<RatingGain> possibleGains, double actualSeconds, boolean correct) {
        if (!correct) return possibleGains.getLast().gain();

        for (RatingGain gain : possibleGains) {
            if (actualSeconds >= gain.start() && actualSeconds <= gain.end()) {
                return gain.gain();
            }
        }

        return possibleGains.getLast().gain();
    }

    private List<RatingGain> getGains(double ratingToGain, double expectedSeconds) {
        return List.of(
                new RatingGain(0, expectedSeconds, ratingToGain),
                new RatingGain(expectedSeconds, (double) 3 / 2 * expectedSeconds, -ratingToGain / 2),
                new RatingGain((double) 3 / 2 * expectedSeconds, Double.MAX_VALUE, -ratingToGain));
    }
}
