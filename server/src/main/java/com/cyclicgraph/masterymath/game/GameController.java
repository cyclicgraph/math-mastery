package com.cyclicgraph.masterymath.game;

import com.cyclicgraph.masterymath.challenge.service.ChallengeService;
import com.cyclicgraph.masterymath.game.model.AnswerRequest;
import com.cyclicgraph.masterymath.game.model.AnswerResponse;
import com.cyclicgraph.masterymath.game.model.ChallengeView;
import com.cyclicgraph.masterymath.game.model.GetChallengesResponse;
import com.cyclicgraph.masterymath.game.model.StartGameRequest;
import com.cyclicgraph.masterymath.game.model.StartGameResponse;
import com.cyclicgraph.masterymath.game.service.GameService;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.digest.DigestUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final ChallengeService challengeService;

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
        var submissions = gameService.getChallenges(gameId);
        var entities = challengeService.findAllById(submissions.stream().map(c -> c.getSnapshot().id()).toList());
        var view = entities.stream().map(e -> new ChallengeView(e.getId(), e.getTexCode(), DigestUtils.md5Hex(e.getAnswer()), submissions.stream().filter(c -> c.getSnapshot().id().equals(e.getId())).findFirst().get().getSnapshot().gains())).toList();
        GetChallengesResponse response = new GetChallengesResponse(gameId, view);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize(value = "hasAuthority('game:answer')")
    @PutMapping(value = "/answer")
    public ResponseEntity<AnswerResponse> answer(@RequestBody @Valid AnswerRequest request) {
        return ResponseEntity.ok(gameService.answer(request.gameId(), request.answers()));
    }
}
