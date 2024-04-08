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
            var actualRoom = allRooms.stream().filter(r -> (r.getCurrent().getId() == socketIOClient.getSessionId())
                    || (r.getOpponent().getId() == socketIOClient.getSessionId())).findAny();
            if (actualRoom.isEmpty())
                throw new RuntimeException("Room not found for socket id: " + socketIOClient.getSessionId());

            log.info("Room {}: Player {} moves: {}", actualRoom.get().getId(),
                    allPlayers.get(socketIOClient.getSessionId()).getName(), gameState.get("state"));
            var rival = allPlayers.get(socketIOClient.getSessionId()).getRival();
            rival.getSocketIOClient().sendEvent("playerMoveFromServer", gameState);
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            if (!allPlayers.containsKey(client.getSessionId())) {
                log.info("Socket id entered: {} ", client.getSessionId());
                allPlayers.put(client.getSessionId(),
                        Player.builder().id(client.getSessionId()).socketIOClient(client).online(true).playing(false).build());
            }
        };
    }

    private DataListener<NickRequest> onRequestToPlay() {
        return (socketIOClient, o, ackRequest) -> {
            log.info("New player waiting in the lobby: {}", o.getPlayerName());
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
                actualPlayer.setPlaying(true);
                opponent.setPlaying(true);

                Room newRoom = Room.builder().id(UUID.randomUUID()).current(actualPlayer).opponent(opponent).build();
                allRooms.add(newRoom);
                log.info("Room {} created for: {} vs {}", newRoom.getId(), newRoom.getCurrent().getName(), newRoom.getOpponent().getName());

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
            var actualRoom = allRooms.stream().filter(r -> (r.getCurrent().getId() == client.getSessionId())
                    || (r.getOpponent().getId() == client.getSessionId())).findAny();

            if (actualPlayer.getRival() != null && actualRoom.isPresent()) {
                actualPlayer.getRival().getSocketIOClient().sendEvent("opponentLeftMatch");
                log.info("Room {} closed: Player {} left the game", actualPlayer.getId(), allPlayers.get(client.getSessionId()).getName());
                allRooms.remove(actualRoom.get());
                allPlayers.remove(client.getSessionId());
            }
        };
    }

}