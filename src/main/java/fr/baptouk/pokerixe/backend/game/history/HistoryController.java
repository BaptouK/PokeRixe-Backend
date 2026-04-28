package fr.baptouk.pokerixe.backend.game.history;

import fr.baptouk.pokerixe.backend.game.Game;
import fr.baptouk.pokerixe.backend.game.provider.GameService;
import fr.baptouk.pokerixe.backend.user.User;
import fr.baptouk.pokerixe.backend.user.provider.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/history")
public final class HistoryController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @GetMapping
    public @ResponseBody ResponseEntity<Iterable<Game>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        final User user = this.userService.getUserByToken(userDetails);

        return ResponseEntity.ok(gameService.getHistory(user.getId()));
    }
    
    @GetMapping("{uuid}")
    public @ResponseBody ResponseEntity<Iterable<Game>> getGameHistory(@AuthenticationPrincipal UserDetails userDetails, @PathVariable final UUID uuid) {
        final User user = this.userService.getUserByToken(userDetails);

        return ResponseEntity.ok(gameService.getHistory(uuid));
    }

}
