package fr.baptouk.pokerixe.backend.game;

import fr.baptouk.pokerixe.backend.game.play.GameCreationResponse;
import fr.baptouk.pokerixe.backend.game.play.GamePlay;
import fr.baptouk.pokerixe.backend.game.provider.exceptions.GameNotFoundException;
import fr.baptouk.pokerixe.backend.game.provider.GameService;
import fr.baptouk.pokerixe.backend.game.provider.exceptions.UserAlreadyInGameException;
import fr.baptouk.pokerixe.backend.game.websocket.packets.game.JoinPacket;
import fr.baptouk.pokerixe.backend.user.User;
import fr.baptouk.pokerixe.backend.user.provider.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

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

    @GetMapping("/available")
    public @ResponseBody ResponseEntity<Iterable<GamePlay>> getAvailableGames() {
        return ResponseEntity.ok(gameService.getAvailableGames());
    }

    @PostMapping
    public @ResponseBody ResponseEntity<GameCreationResponse> createGame(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String description, @RequestParam Integer pokemonTeamSlot) {
        final User user = this.userService.getUserByToken(userDetails);

        try {
            return ResponseEntity.ok(gameService.createGame(user, description, pokemonTeamSlot));
        } catch (UserAlreadyInGameException e) {
            logger.warn("User Already In Game Exception");
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("{gameId}/join")
    public @ResponseBody ResponseEntity<String> joinGame(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID gameId, @RequestParam int selectSlotPokemon) {
        final User user = this.userService.getUserByToken(userDetails);

        try {
            return ResponseEntity.ok(gameService.joinGame(user, gameId, selectSlotPokemon));
        } catch (GameNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
