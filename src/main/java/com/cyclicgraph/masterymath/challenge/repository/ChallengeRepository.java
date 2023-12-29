package com.cyclicgraph.masterymath.challenge.repository;

import com.cyclicgraph.masterymath.challenge.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
}
