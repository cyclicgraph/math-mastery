package com.cyclicgraph.masterymath.game.service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RatingCalculator {
    private final double challengeRating;
    private final double peakRating;
    private final int k;

    public RatingCalculator(double rating, double peakRating) {
        this.challengeRating = rating;
        this.peakRating = peakRating;
        this.k = (this.peakRating > 2400) ? 10 : 20;
    }


    public double calculateGain(double opponentRating) {
        double expectedScore = 1 / (1 + Math.pow(10, (opponentRating - challengeRating) / 400));

        return k * (1 - expectedScore);
    }
}
