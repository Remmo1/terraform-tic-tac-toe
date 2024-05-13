package tic.tac.toe.game.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class Room {
    private UUID id;
    private Player cross;
    private Player circle;
    private String move;
    private LocalDateTime startTime;
}
