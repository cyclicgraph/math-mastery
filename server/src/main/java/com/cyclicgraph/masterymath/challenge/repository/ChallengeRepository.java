package com.cyclicgraph.masterymath.challenge.repository;

import com.cyclicgraph.masterymath.challenge.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query(value = "SELECT c FROM Challenge c WHERE c.statistics.rating BETWEEN :minRating AND :maxRating AND c.enabled = TRUE ORDER BY RANDOM() LIMIT :limit")
    List<Challenge> findRandomChallengesWithinRatingRange(double minRating, double maxRating, int limit);
}
