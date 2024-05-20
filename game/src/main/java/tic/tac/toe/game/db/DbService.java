package tic.tac.toe.game.db;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DbService {

    private final ResultRepository resultRepository;

    @Transactional
    public Result saveResult(UUID roomId, String circlePlayer, String crossPlayer, LocalDateTime startTime, LocalDateTime endTime, String moves) {
        if (resultRepository.findById(roomId).isEmpty()) {
            var newResult = new Result();
            newResult.setId(roomId);
            newResult.setCirclePlayer(circlePlayer);
            newResult.setCrossPlayer(crossPlayer);
            newResult.setStartTime(startTime);
            newResult.setEndTime(endTime);
            newResult.setMoves(moves);
            return resultRepository.save(newResult);
        }
        return null;
    }

    public List<ResultDto> getAllResultsForPlayer() {
        var playerNick = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials()).getClaims().get("username").toString();
        return resultRepository.findAllByCirclePlayerOrCrossPlayer(playerNick, playerNick).stream().map(
                r -> new ResultDto(r.getId(), r.getCirclePlayer(), r.getCrossPlayer(), r.getMoves(), r.getStartTime(), r.getEndTime())
        ).toList();
    }

}
