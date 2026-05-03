package fr.baptouk.pokerixe.backend.game;

import fr.baptouk.pokerixe.backend.game.analysis.GameAnalysis;
import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.game.turn.Turn;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "game")
@Data
public class Game {

    @Id
    private UUID id = UUID.randomUUID();

    private String description;

    private List<Turn> turns = new ArrayList<>();

    private List<GamePlayer> players = new ArrayList<>(2);

    private GameAnalysis analysis;

    private LocalDateTime date;

    private UUID winnerId;

    protected Game(final String description) {
        this.description = description;
    }

    public Game addPlayer(final GamePlayer player) {
        this.players.add(player);
        return this;
    }
}
