package tic.tac.toe.game.db;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/result")
@AllArgsConstructor
public class ResultController {

    private final DbService dbService;

    @GetMapping
    @CrossOrigin(origins = "*")
    public ResponseEntity<List<ResultDto>> getResultsForPlayer() {
        return ResponseEntity.ok(dbService.getAllResultsForPlayer());
    }
}
