package com.cyclicgraph.masterymath.challenge.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateChallengeRequest(@NotBlank String texCode, @NotBlank String answer, @NotNull @Positive double initialRating,
                                     @NotNull ChallengeMetadata.ChallengeType challengeType) {
}
