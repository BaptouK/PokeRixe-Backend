package fr.baptouk.pokerixe.backend.game.team;

import fr.baptouk.pokerixe.backend.game.pokemon.GamePokemon;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class GameTeam {

    private UUID id = UUID.randomUUID();

    private List<GamePokemon> pokemons = Collections.emptyList();

}
