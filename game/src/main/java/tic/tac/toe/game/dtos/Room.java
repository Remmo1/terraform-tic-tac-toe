package tic.tac.toe.game.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Room {
    private Player currentPlayer;
    private Player opponent;
}
