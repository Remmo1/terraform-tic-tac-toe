package tic.tac.toe.game;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tic.tac.toe.game.dtos.NickRequest;
import tic.tac.toe.game.dtos.Player;
import tic.tac.toe.game.dtos.Room;

import java.util.*;

@Slf4j
@Component
public class SocketModule {

    private final Map<UUID, Player> allPlayers = new HashMap<>();
    private final List<Room> allRooms = new ArrayList<>();

    public SocketModule(SocketIOServer server) {
        server.addConnectListener(this.onConnected());
        server.addDisconnectListener(this.onDisconnected());
        server.addEventListener("request_to_play", NickRequest.class, this.onRequestToPlay());
        server.addEventListener("playerMoveFromClient", Map.class, this.onPlayerMoveFromClient());
    }

    private DataListener<Map> onPlayerMoveFromClient() {
        return (socketIOClient, gameState, ackRequest) -> {
            log.info("Move: {}", gameState.get("state"));
            var rival = allPlayers.get(socketIOClient.getSessionId()).getRival();
            rival.getSocketIOClient().sendEvent("playerMoveFromServer", gameState);
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            log.info("Socket id entered: {} ", client.getSessionId());
            allPlayers.put(client.getSessionId(),
                    Player.builder().id(client.getSessionId()).socketIOClient(client).online(true).playing(false).build());
        };
    }

    private DataListener<NickRequest> onRequestToPlay() {
        return (socketIOClient, o, ackRequest) -> {
            log.info("New player: {}", o.getPlayerName());
            var actualPlayer = allPlayers.get(socketIOClient.getSessionId());
            actualPlayer.setName(o.getPlayerName());
            Player opponent = null;

            for (var playerId : allPlayers.entrySet()) {
                var player = allPlayers.get(playerId.getKey());
                if (player.getOnline() && !player.getPlaying() &&
                        socketIOClient.getSessionId() != playerId.getKey() && player.getName() != null) {
                    opponent = player;
                    break;
                }
            }

            if (opponent != null) {
                actualPlayer.setRival(opponent);
                opponent.setRival(actualPlayer);
                allRooms.add(Room.builder().currentPlayer(opponent).currentPlayer(actualPlayer).build());

                var playerJson = Map.of(
                        "opponentName", opponent.getName(),
                        "playingAs", "circle"
                );
                var opponentJson = Map.of(
                        "opponentName", actualPlayer.getName(),
                        "playingAs", "cross"
                );

                socketIOClient.sendEvent("OpponentFound", playerJson);
                opponent.getSocketIOClient().sendEvent("OpponentFound", opponentJson);
            } else {
                socketIOClient.sendEvent("OpponentNotFound");
            }

        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            log.info("Socket id left: {} ", client.getSessionId());
            var actualPlayer = allPlayers.get(client.getSessionId());

            actualPlayer.setOnline(false);
            actualPlayer.setPlaying(false);

            if (actualPlayer.getRival() != null) {
                actualPlayer.getRival().getSocketIOClient().sendEvent("opponentLeftMatch");
            }
        };
    }

}