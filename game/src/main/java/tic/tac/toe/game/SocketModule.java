package tic.tac.toe.game;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tic.tac.toe.game.db.DbService;
import tic.tac.toe.game.dtos.NickRequest;
import tic.tac.toe.game.dtos.Player;
import tic.tac.toe.game.dtos.Room;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class SocketModule {

    private final Map<UUID, Player> allPlayers = new HashMap<>();
    private final List<Room> allRooms = new ArrayList<>();

    private final DbService dbService;

    public SocketModule(SocketIOServer server, DbService dbService) {
        this.dbService = dbService;
        server.addConnectListener(this.onConnected());
        server.addDisconnectListener(this.onDisconnected());
        server.addEventListener("request_to_play", NickRequest.class, this.onRequestToPlay());
        server.addEventListener("playerMoveFromClient", Map.class, this.onPlayerMoveFromClient());
        server.addEventListener("endGame", Map.class, this.onEndGame());
    }

    private DataListener<Map> onEndGame() {
        return (client, data, ackSender) -> {
            var actualRoom = findRoomForPlayer(client.getSessionId());
            client.disconnect();
            if (actualRoom == null) {
                return;
            }

            var result = data.get("result");
            if (result.equals("draw")) {
                log.info("Room {}: it's a draw!", actualRoom.getId());
                result = 'd';
            } else {
                log.info("Room {}: {} won the game ", actualRoom.getId(), result);
                if (result.equals("circle")) result = 'o';
                else result = 'x';
            }
            actualRoom.setMove(actualRoom.getMove() + result);

            synchronized (this) {
                dbService.saveResult(actualRoom.getId(), actualRoom.getCross().getName(), actualRoom.getCircle().getName(), actualRoom.getStartTime(), LocalDateTime.now(), actualRoom.getMove());
            }
            allRooms.removeIf(r -> r.getId() == actualRoom.getId());
        };
    }

    private DataListener<Map> onPlayerMoveFromClient() {
        return (socketIOClient, gameState, ackRequest) -> {
            var actualRoom = findRoomForPlayer(socketIOClient.getSessionId());

            log.info("Room {}: Player {} moves: {}", actualRoom.getId(),
                    allPlayers.get(socketIOClient.getSessionId()).getName(), gameState.get("state"));
            actualRoom.setMove(actualRoom.getMove() + ((HashMap) gameState.get("state")).get("id").toString());
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

                Room newRoom = Room.builder().id(UUID.randomUUID()).cross(actualPlayer).circle(opponent).startTime(LocalDateTime.now()).move("").build();
                allRooms.add(newRoom);
                log.info("Room {} created for: {} vs {}", newRoom.getId(), newRoom.getCross().getName(), newRoom.getCircle().getName());

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
            UUID clientId = client.getSessionId();
            actualPlayer.getRival().getSocketIOClient().sendEvent("opponentLeftMatch");
            allPlayers.remove(client.getSessionId());

//            var actualRoom = findRoomForPlayer(clientId);
//            if (actualRoom != null) {
//                log.info("Room {} closed: Player {} left the game", actualPlayer.getId(), allPlayers.get(clientId).getName());
//                allRooms.remove(actualRoom);
//            }
        };
    }

    private Room findRoomForPlayer(UUID socketId) {
        var actualRoom = allRooms.stream().filter(r -> (r.getCross().getId() == socketId)
                || (r.getCircle().getId() == socketId)).findAny();
        return actualRoom.orElse(null);
    }

    private Player getRival(UUID socketId) {
        var actualPlayer = allPlayers.get(socketId);
        if (Boolean.TRUE.equals(actualPlayer.getPlaying())) {
            return actualPlayer.getRival();
        }
        return null;
    }

}