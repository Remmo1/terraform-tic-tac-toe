package tic.tac.toe.game.db;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResultRepository extends JpaRepository<Result, UUID> {
    List<Result> findAllByCirclePlayerOrCrossPlayer(String circlePlayer, String crossPlayer);
}
