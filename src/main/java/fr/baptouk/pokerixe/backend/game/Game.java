package fr.baptouk.pokerixe.backend.game;

import fr.baptouk.pokerixe.backend.game.analysis.GameAnalysis;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.turn.Turn;
import fr.baptouk.pokerixe.backend.user.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Document(collection = "game")
@Data
public class Game {

    @Id
    private UUID id = UUID.randomUUID();

    private String description;

    private List<Turn> turns = Collections.emptyList();

    private List<GamePlayer> players = new ArrayList<>(2);

    private GameAnalysis analysis;

    protected Game(final String description) {
        this.description = description;
    }

    public Game addPlayer(final User user) {
        this.players.add(GamePlayer.builder()
                .id(user.getId())
                .pseudo(user.getPseudo())
                .team(user.getTeam())
                .selectedPokemon(user.getTeam()
                        .getPokemons()
                        .stream()
                        .findFirst()
                        .orElse(null))
                .build());

        return this;
    }

}
