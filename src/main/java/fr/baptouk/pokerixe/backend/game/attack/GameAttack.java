package fr.baptouk.pokerixe.backend.game.attack;

import fr.baptouk.pokerixe.backend.game.pokemon.PokemonType;
import lombok.Data;

@Data
public class GameAttack {

    private String name;
    private Integer power;
    private PokemonType type;
    private Integer accuracy;   // int entre 0 et 100
    private PowerClass powerClass;

}
