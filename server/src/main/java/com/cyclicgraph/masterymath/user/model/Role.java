package com.cyclicgraph.masterymath.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.cyclicgraph.masterymath.user.model.Permission.*;

@RequiredArgsConstructor
public enum Role {
    ADMIN(Set.of(CHALLENGE_CREATE, CHALLENGE_MODIFY, CHALLENGE_READ, GAME_READ, GAME_STOP)),
    USER(Set.of(GAME_START, GAME_READ, GAME_ANSWER, GAME_STOP));

    @Getter
    private final Set<Permission> permissions;

    public Set<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = this.permissions.stream().map(p -> new SimpleGrantedAuthority(p.getName())).collect(Collectors.toSet());

        // despite authorities for permissions - add authority based on given role because of Spring's requirement
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }
}
