package fr.baptouk.pokerixe.backend.game.turn.action;

import fr.baptouk.pokerixe.backend.game.gameplayer.GamePlayer;
import fr.baptouk.pokerixe.backend.team.pokemon.Pokemon;

public class Switch extends Action{

    public Switch() {
        super("switch");
    }

    private GamePlayer player;

    private Pokemon nextPokemon;

}
