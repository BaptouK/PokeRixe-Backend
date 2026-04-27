package fr.baptouk.pokerixe.backend.game.history;

import fr.baptouk.pokerixe.backend.game.Game;
import fr.baptouk.pokerixe.backend.game.provider.GameService;
import fr.baptouk.pokerixe.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/history")
public final class HistoryController {

    @Autowired
    private GameService gameService;

    @GetMapping
    public @ResponseBody ResponseEntity<Iterable<Game>> getHistory() {
        final User user = null; // TODO : Get user by token

        return ResponseEntity.ok(gameService.getHistory(user.getId()));
    }
    
    @GetMapping("{uuid}")
    public @ResponseBody ResponseEntity<Iterable<Game>> getGameHistory(@PathVariable final UUID uuid) {
        final User user = null; // TODO : Get user by token

        return ResponseEntity.ok(gameService.getHistory(uuid));
    }

}
