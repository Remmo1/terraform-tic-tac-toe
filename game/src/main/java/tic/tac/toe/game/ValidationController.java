package tic.tac.toe.game;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {

    @GetMapping("/validate")
    public ResponseEntity<String> validate() {
        return ResponseEntity.ok("OK");
    }

}
