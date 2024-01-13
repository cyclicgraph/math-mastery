package com.cyclicgraph.masterymath.game.model;

import jakarta.validation.constraints.Size;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static com.cyclicgraph.masterymath.game.service.GameService.CHALLENGES_IN_GAME;

public record AnswerRequest(@NotNull UUID gameId,
                            // fixed number of answers is probably not a good idea, but it's good enough for now
                            @NotNull @Size(min = CHALLENGES_IN_GAME, max = CHALLENGES_IN_GAME) List<AnswerRequestEntry> answers) {
}

