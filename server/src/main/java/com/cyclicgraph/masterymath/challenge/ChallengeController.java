package com.cyclicgraph.masterymath.challenge;

import com.cyclicgraph.masterymath.challenge.model.CreateChallengeRequest;
import com.cyclicgraph.masterymath.challenge.model.CreateChallengeResponse;
import com.cyclicgraph.masterymath.challenge.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/challenge")
public class ChallengeController {
    private final ChallengeService challengeService;

    @PreAuthorize(value = "hasAuthority('challenge:create')")
    @PostMapping
    public ResponseEntity<CreateChallengeResponse> createChallenge(@RequestBody @Valid CreateChallengeRequest request) {
        return ResponseEntity.ok(new CreateChallengeResponse(challengeService.createChallenge(request).getId()));
    }
}
