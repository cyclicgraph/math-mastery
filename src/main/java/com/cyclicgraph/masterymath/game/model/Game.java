package com.cyclicgraph.masterymath.game.model;

import com.cyclicgraph.masterymath.challenge.model.Challenge;
import com.cyclicgraph.masterymath.challenge.model.ChallengeState;
import com.cyclicgraph.masterymath.user.model.UserEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyJoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ElementCollection
    @CollectionTable(name = "game_challenge_state", joinColumns = @JoinColumn(name = "game_id"))
    @MapKeyJoinColumn(name = "challenge_id")
    @Column(name = "challenge_state")
    @Enumerated(EnumType.STRING)
    private Map<Challenge, ChallengeState> challenges = new HashMap<>();

    private boolean rated;

    @ManyToOne
    private UserEntity user;

    public Game(UUID id, List<Challenge> challenges, boolean rated, UserEntity user) {
        this.id = id;
        this.challenges = challenges.stream()
                .collect(Collectors.toMap(Function.identity(), challenge -> ChallengeState.NONE));
        this.rated = rated;
        this.user = user;
    }
}
