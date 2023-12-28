package com.cyclicgraph.masterymath.auth.model;

import com.cyclicgraph.masterymath.user.model.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private long expires;

    private UUID token;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return expires == that.expires && Objects.equals(id, that.id) && Objects.equals(token, that.token) && Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expires, token, owner);
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "id=" + id +
                ", expires=" + expires +
                ", token=" + token +
                ", owner=" + owner +
                '}';
    }
}
