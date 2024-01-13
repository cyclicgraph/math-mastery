package com.cyclicgraph.masterymath.game.model;

import java.util.List;
import java.util.UUID;

public record AnswerResponse(UUID gameId, List<AnswerResponseEntry> entries, double ratingGain) {
}

