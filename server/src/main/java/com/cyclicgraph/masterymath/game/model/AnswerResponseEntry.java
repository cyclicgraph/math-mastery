package com.cyclicgraph.masterymath.game.model;


public record AnswerResponseEntry(long challengeId, boolean correct, double ratingGain) {
}
