package com.cyclicgraph.masterymath.game;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/game")
public class GameController {

    @PostMapping(value = "/start")
    public ResponseEntity<Void> start() {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/stop")
    public ResponseEntity<Void> stop() {
        return ResponseEntity.ok().build();
    }
}
