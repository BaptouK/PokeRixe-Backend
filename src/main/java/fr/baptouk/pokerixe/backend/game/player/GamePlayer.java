package fr.baptouk.pokerixe.backend.game.player;

import fr.baptouk.pokerixe.backend.game.team.GameTeam;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public final class GamePlayer {

    private UUID id;

    private String pseudo;

    private GameTeam team;

    private Integer indexSelectedPokemon;

    private transient String sessionId;

}
