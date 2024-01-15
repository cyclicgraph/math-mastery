package com.cyclicgraph.masterymath.user;

import com.cyclicgraph.masterymath.user.model.UserCompact;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import com.cyclicgraph.masterymath.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/user")
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/me")
    public ResponseEntity<UserCompact> me() {
        UserEntity current = userService.getCurrent();
        UserCompact me = new UserCompact(current.getId().toString(), current.getUsername(), current.getRoles(), current.getChallengeRating(), current.getPeakRating());

        return ResponseEntity.ok(me);
    }
}
