package fr.baptouk.pokerixe.backend.game.history;

import fr.baptouk.pokerixe.backend.game.Game;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.provider.GameService;
import fr.baptouk.pokerixe.backend.user.User;
import fr.baptouk.pokerixe.backend.user.provider.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games/history")
public final class HistoryController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @GetMapping
    public @ResponseBody ResponseEntity<List<GameHistoryEntryDTO>> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        final User user = this.userService.getUserByToken(userDetails);

        List<GameHistoryEntryDTO> history = gameService.getHistory(user.getId())
                .stream()
                .filter(game -> game.getWinnerId() != null && game.getDate() != null)
                .map(game -> toDTO(game, user.getId()))
                .toList();

        return ResponseEntity.ok(history);
    }

    private GameHistoryEntryDTO toDTO(Game game, UUID userId) {
        GamePlayer player = game.getPlayers().stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElseThrow();

        GamePlayer opponent = game.getPlayers().stream()
                .filter(p -> !p.getId().equals(userId))
                .findFirst()
                .orElseThrow();

        String result = game.getWinnerId().equals(userId) ? "win" : "loss";

        List<HistoryPokemonDTO> playerTeam = player.getTeam().getPokemons().stream()
                .map(p -> new HistoryPokemonDTO(p.getPokemonId(), p.getName(), p.getUrlImageFront(), p.getHp() <= 0))
                .toList();

        List<HistoryPokemonDTO> opponentTeam = opponent.getTeam().getPokemons().stream()
                .map(p -> new HistoryPokemonDTO(p.getPokemonId(), p.getName(), p.getUrlImageFront(), p.getHp() <= 0))
                .toList();

        return new GameHistoryEntryDTO(
                game.getId().toString(),
                game.getDate().toString(),
                opponent.getPseudo(),
                result,
                game.getTurns().size(),
                playerTeam,
                opponentTeam,
                game.getTurns()
        );
    }
}
