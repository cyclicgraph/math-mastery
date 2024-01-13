package com.cyclicgraph.masterymath.challenge.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@Entity
@Filter(
        name = "enabledOnlyFilter",
        condition = "enabled = true"
)
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "challenge_gen")
    @SequenceGenerator(name = "challenge_gen", sequenceName = "challenge_seq", allocationSize = 1)
    private Long id;

    private String texCode;

    private String answer;

    @Embedded
    private ChallengeStatistics statistics;

    @Embedded
    private ChallengeMetadata metadata;

    private boolean enabled = true;

    public void updateAfterAnswer(boolean answeredCorrectly, double ratingGain) {
        // this code is for sure not thread-safe
        statistics.setRating(statistics.getRating() + ratingGain);
        statistics.setPeakRating(Math.max(statistics.getPeakRating(), statistics.getRating()));
        statistics.setTotalAnswers(statistics.getTotalAnswers() + 1);
        statistics.setAnsweredCorrectly(statistics.getAnsweredCorrectly() + (answeredCorrectly ? 1 : 0));
    }
}