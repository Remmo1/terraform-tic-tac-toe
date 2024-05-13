package tic.tac.toe.game.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Result {
    @Id
    private UUID id;
    private String circlePlayer;
    private String crossPlayer;
    private String moves;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
