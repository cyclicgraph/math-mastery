package com.cyclicgraph.masterymath.game;

import com.cyclicgraph.masterymath.game.model.ChallengeView;
import com.cyclicgraph.masterymath.game.model.GetChallengesResponse;
import com.cyclicgraph.masterymath.game.model.StartGameRequest;
import com.cyclicgraph.masterymath.game.model.StartGameResponse;
import com.cyclicgraph.masterymath.game.service.GameService;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.digest.DigestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/game")
public class GameController {
    private final GameService gameService;

    @PreAuthorize(value = "hasAuthority('game:start')")
    @PostMapping(value = "/start")
    public ResponseEntity<StartGameResponse> start(@RequestBody StartGameRequest request) {
        return ResponseEntity.ok(new StartGameResponse(gameService.start(request.rated())));
    }

    @PreAuthorize(value = "hasAuthority('game:stop')")
    @PostMapping(value = "/stop")
    public ResponseEntity<Void> stop() {
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(value = "hasAuthority('game:read')")
    @GetMapping(value = "/challenges")
    public ResponseEntity<GetChallengesResponse> getChallenges(@RequestParam UUID gameId) {
        var challenges = gameService.getChallenges(gameId);
        GetChallengesResponse response = new GetChallengesResponse(gameId, challenges.stream().map(challenge -> new ChallengeView(challenge.getId(), challenge.getTexCode(), DigestUtils.md5Hex(challenge.getAnswer()))).toList());

        return ResponseEntity.ok(response);
    }
}
