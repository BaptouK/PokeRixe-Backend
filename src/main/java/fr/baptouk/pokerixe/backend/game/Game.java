package fr.baptouk.pokerixe.backend.game;

import fr.baptouk.pokerixe.backend.game.analysis.GameAnalysis;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.turn.Turn;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "game")
public final class Game {

    @Id
    private UUID id;

    private List<Turn> turns;

    private List<GamePlayer> players;

    private GameAnalysis analysis;

}
