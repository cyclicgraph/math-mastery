package com.cyclicgraph.masterymath.user.model;

import java.util.Collection;

public record UserCompact(String id, String username, Collection<Role> roles, double challengeRating, double peakRating) {
}
