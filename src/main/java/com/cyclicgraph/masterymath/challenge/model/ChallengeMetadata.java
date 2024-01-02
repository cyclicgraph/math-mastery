package com.cyclicgraph.masterymath.challenge.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Embeddable
@Data
// not sure if it would be needed for any functionality
public class ChallengeMetadata {
    @Enumerated(EnumType.STRING)
    private ChallengeType challengeType;
    private double initialRating;

    public enum ChallengeType {
        BASIC_OPERATION, COMPOSITE_OPERATION, INTEGRAL, NTH_ROOT
    }
}
