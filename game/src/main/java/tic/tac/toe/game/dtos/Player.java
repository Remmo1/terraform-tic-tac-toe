package tic.tac.toe.game.dtos;

import com.corundumstudio.socketio.SocketIOClient;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Player {
    private UUID id;
    private SocketIOClient socketIOClient;
    private String name;
    private Player rival;
    private Boolean online;
    private Boolean playing;
}
