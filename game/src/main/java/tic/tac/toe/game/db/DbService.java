package tic.tac.toe.game.db;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

}
