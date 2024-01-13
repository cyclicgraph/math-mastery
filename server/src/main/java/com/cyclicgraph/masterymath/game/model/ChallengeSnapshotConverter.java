package com.cyclicgraph.masterymath.game.model;

import com.cyclicgraph.masterymath.challenge.model.ChallengeGainsSnapshot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;

@Converter
public class ChallengeSnapshotConverter implements AttributeConverter<ChallengeGainsSnapshot, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ChallengeGainsSnapshot challengeGainsSnapshot) {
        try {
            return objectMapper.writeValueAsString(challengeGainsSnapshot);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ChallengeGainsSnapshot convertToEntityAttribute(String string) {
        try {
            return objectMapper.readValue(string, ChallengeGainsSnapshot.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
