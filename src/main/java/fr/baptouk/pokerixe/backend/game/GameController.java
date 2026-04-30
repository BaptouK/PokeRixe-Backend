package fr.baptouk.pokerixe.backend.game;

import fr.baptouk.pokerixe.backend.game.provider.exceptions.GameNotFoundException;
import fr.baptouk.pokerixe.backend.game.provider.GameService;
import fr.baptouk.pokerixe.backend.game.provider.exceptions.UserAlreadyInGameException;
import fr.baptouk.pokerixe.backend.user.User;
import fr.baptouk.pokerixe.backend.user.provider.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/games")
public final class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @GetMapping
    public @ResponseBody ResponseEntity<Iterable<Game>> getGames() {
        return ResponseEntity.ok(gameService.getGames());
    }

    @GetMapping("{gameId}")
    public @ResponseBody ResponseEntity<Game> getGame(@PathVariable final UUID gameId) {
        try {
            return ResponseEntity.ok(gameService.getGame(gameId));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("available")
    public @ResponseBody ResponseEntity<Iterable<Game>> getAvailableGames() {
        return ResponseEntity.ok(gameService.getAvailableGames());
    }

    @PostMapping
    public @ResponseBody ResponseEntity<String> createGame(@AuthenticationPrincipal UserDetails userDetails, @RequestBody String description) {
        final User user = this.userService.getUserByToken(userDetails);

        try {
            return ResponseEntity.ok(gameService.createGame(user, description));
        } catch (UserAlreadyInGameException e) {
            System.out.println(">>> UserAlreadyInGameException !");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("{gameId}/join")
    public @ResponseBody ResponseEntity<String> joinGame(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UUID gameId) {
        final User user = this.userService.getUserByToken(userDetails);

        try {
            return ResponseEntity.ok(gameService.joinGame(user, gameId));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
