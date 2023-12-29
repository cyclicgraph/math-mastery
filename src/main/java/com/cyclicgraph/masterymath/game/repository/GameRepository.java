package com.cyclicgraph.masterymath.game.repository;

import com.cyclicgraph.masterymath.game.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}
