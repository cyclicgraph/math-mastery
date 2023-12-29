package com.cyclicgraph.masterymath.challenge.service;

import com.cyclicgraph.masterymath.challenge.model.Challenge;
import com.cyclicgraph.masterymath.challenge.model.ChallengeMetadata;
import com.cyclicgraph.masterymath.challenge.model.ChallengeStatistics;
import com.cyclicgraph.masterymath.challenge.model.CreateChallengeRequest;
import com.cyclicgraph.masterymath.challenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public Challenge createChallenge(CreateChallengeRequest request) {
        Challenge challenge = new Challenge();
        challenge.setAnswer(request.answer());
        challenge.setTexCode(request.texCode());
        // check if tex code compiles
        validateTexCode(request.texCode());

        ChallengeStatistics statistics = new ChallengeStatistics();
        statistics.setRating(request.initialRating());
        challenge.setStatistics(statistics);

        ChallengeMetadata metadata = new ChallengeMetadata();
        metadata.setChallengeType(request.challengeType());
        metadata.setInitialRating(request.initialRating());
        challenge.setMetadata(metadata);

        return challengeRepository.save(challenge);
    }

    private void validateTexCode(String code) {
        // throws an exception on wrong code
        TeXFormula teXFormula = new TeXFormula(code);

        // one and only one equation sign is allowed
        int equationSigns = StringUtils.countMatches(code, "=");
        if (equationSigns != 1) {
            throw new IllegalStateException("number of equation signs must be 1, provided " + equationSigns);
        }

        // need more validation in future
    }
}
