package tic.tac.toe.game.db;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ResultDto {
    private UUID id;
    private String circlePlayer;
    private String crossPlayer;
    private String moves;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
