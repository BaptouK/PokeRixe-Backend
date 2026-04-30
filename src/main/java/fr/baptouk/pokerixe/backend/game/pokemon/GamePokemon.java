package fr.baptouk.pokerixe.backend.game.pokemon;

import fr.baptouk.pokerixe.backend.game.attack.GameAttack;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class GamePokemon {

    private UUID id = UUID.randomUUID();
    private Integer pokemonId;

    private String name;

    private List<PokemonType> type = Collections.emptyList();

    private List<PokemonType> superEffectivesTypes  = Collections.emptyList();
    private List<PokemonType> notVeryEffective= Collections.emptyList();
    private List<PokemonType> noDamage = Collections.emptyList();

    private List<GameAttack> attacks = Collections.emptyList();

    private String urlImageFront;
    private String urlImageBack;

}
