package com.cyclicgraph.masterymath.game.service;

import com.cyclicgraph.masterymath.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
}
