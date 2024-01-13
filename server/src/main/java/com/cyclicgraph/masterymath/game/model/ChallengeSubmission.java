package com.cyclicgraph.masterymath.game.model;

import com.cyclicgraph.masterymath.challenge.model.ChallengeGainsSnapshot;
import com.cyclicgraph.masterymath.challenge.model.ChallengeState;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class ChallengeSubmission {
    @Convert(converter = ChallengeSnapshotConverter.class)
    private ChallengeGainsSnapshot snapshot;

    @Enumerated(value = EnumType.STRING)
    private ChallengeState state;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ChallengeSubmission) obj;
        return Objects.equals(this.snapshot, that.snapshot) &&
                Objects.equals(this.state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snapshot, state);
    }

    @Override
    public String toString() {
        return "ChallengeSubmission[" +
                "snapshot=" + snapshot + ", " +
                "state=" + state + ']';
    }

}