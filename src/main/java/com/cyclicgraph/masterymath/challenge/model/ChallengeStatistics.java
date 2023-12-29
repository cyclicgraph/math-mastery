package com.cyclicgraph.masterymath.challenge.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ChallengeStatistics {
    private double expectedSeconds;

    private long totalAnswers;

    private long answeredCorrectly;

    private double rating;
}
