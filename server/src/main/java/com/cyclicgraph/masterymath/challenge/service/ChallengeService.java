package com.cyclicgraph.masterymath.challenge.service;

import com.cyclicgraph.masterymath.challenge.model.Challenge;
import com.cyclicgraph.masterymath.challenge.model.ChallengeMetadata;
import com.cyclicgraph.masterymath.challenge.model.ChallengeStatistics;
import com.cyclicgraph.masterymath.challenge.model.CreateChallengeRequest;
import com.cyclicgraph.masterymath.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.scilab.forge.jlatexmath.ParseException;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    /**
     * Creates and validates a new Challenge based on the provided request details.
     *
     * @param request CreateChallengeRequest containing the details required to create a new challenge,
     *                including answerHash, TeX code, initial rating, and challenge type.
     * @return The Challenge entity that was saved in the database.
     * @throws org.scilab.forge.jlatexmath.ParseException If the provided TeX code does not compile or is invalid.
     * @throws IllegalArgumentException                   If number of equation signs in TeX code is not 1.
     */
    public Challenge createChallenge(CreateChallengeRequest request) throws ParseException, IllegalArgumentException {
        Challenge challenge = new Challenge();
        challenge.setAnswer(request.answer());
        challenge.setTexCode(request.texCode());
        // check if tex code compiles
        validateTexCode(request.texCode());

        ChallengeStatistics statistics = new ChallengeStatistics();
        statistics.setRating(request.initialRating());
        statistics.setPeakRating(request.initialRating());
        challenge.setStatistics(statistics);

        ChallengeMetadata metadata = new ChallengeMetadata();
        metadata.setChallengeType(request.challengeType());
        metadata.setInitialRating(request.initialRating());
        challenge.setMetadata(metadata);

        return challengeRepository.save(challenge);
    }

    /**
     * Draws a random list of challenges.
     * Each challenge will have a rating within the range of [rating - 100, rating + 100].
     *
     * @param numberOfChallenges the number of random challenges to draw.
     * @param rating             the central rating around which challenges are drawn.
     * @return a list of randomly selected challenges within the specified rating range.
     * @throws IllegalStateException if not enough challenges could be drawn.
     */
    public List<Challenge> drawRandom(int numberOfChallenges, double rating) throws IllegalStateException {
        double lowerBound = rating - 100;
        double upperBound = rating + 100;
        List<Challenge> challenges = challengeRepository.findRandomChallengesWithinRatingRange(lowerBound, upperBound, numberOfChallenges);
        if (challenges.size() < numberOfChallenges)
            throw new IllegalStateException("not enough challenges drawn, expected " + numberOfChallenges + ", but was " + challenges.size());

        return challenges;
    }

    private void validateTexCode(String code) {
        // throws an exception on wrong code
        TeXFormula teXFormula = new TeXFormula(code);

        // one and only one equation sign is allowed
        int equationSigns = StringUtils.countMatches(code, "=");
        if (equationSigns != 1) {
            throw new IllegalArgumentException("number of equation signs must be 1, provided " + equationSigns);
        }

        // need more validation in future
    }

    public List<Challenge> findAllById(List<Long> ids) {
        return challengeRepository.findAllById(ids);
    }
}
