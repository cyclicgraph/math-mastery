package com.cyclicgraph.masterymath.game.model;

import jakarta.validation.constraints.Min;
import org.jetbrains.annotations.NotNull;

public record AnswerRequestEntry(long challengeId, @NotNull String answer, @Min(0) double seconds) {
}
