package tic.tac.toe.game.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Room {
    private UUID id;
    private Player current;
    private Player opponent;
    private String move;
}
