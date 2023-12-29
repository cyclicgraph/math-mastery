package com.cyclicgraph.masterymath.challenge.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "challenge_gen")
    @SequenceGenerator(name = "challenge_gen", sequenceName = "challenge_seq")
    private Long id;

    private String texCode;

    private String answer;

    @Embedded
    private ChallengeStatistics statistics;

    @Embedded
    private ChallengeMetadata metadata;
}