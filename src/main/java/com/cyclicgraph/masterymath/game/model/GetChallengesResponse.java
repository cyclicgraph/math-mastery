package com.cyclicgraph.masterymath.game.model;

import java.util.Collection;
import java.util.UUID;

public record GetChallengesResponse(UUID gameId, Collection<ChallengeView> challenges) {
}
