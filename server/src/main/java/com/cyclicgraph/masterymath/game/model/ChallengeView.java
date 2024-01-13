package com.cyclicgraph.masterymath.game.model;

import com.cyclicgraph.masterymath.challenge.model.RatingGain;

import java.util.List;

public record ChallengeView(long id, String texCode, String hashedAnswer, List<RatingGain> gains) {
}
