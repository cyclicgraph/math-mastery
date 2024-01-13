package com.cyclicgraph.masterymath.auth.model;

import java.util.Date;

public record Jwt(String token, Date expiration) {
}
