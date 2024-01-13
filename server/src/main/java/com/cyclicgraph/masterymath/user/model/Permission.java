package com.cyclicgraph.masterymath.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Permission {
    GAME_READ("game:read"),
    GAME_START("game:start"),
    GAME_ANSWER("game:answer"),
    GAME_STOP("game:stop"),

    CHALLENGE_CREATE("challenge:create"),
    CHALLENGE_MODIFY("challenge:modify"),
    CHALLENGE_READ("challenge:read")
    ;

    private final String name;
}
