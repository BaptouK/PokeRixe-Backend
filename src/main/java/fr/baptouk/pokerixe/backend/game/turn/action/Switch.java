package fr.baptouk.pokerixe.backend.game.turn.action;

import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import fr.baptouk.pokerixe.backend.user.team.pokemon.Pokemon;

public final class Switch extends Action{

    public Switch() {
        super("switch");
    }

    private GamePlayer player;

    private Pokemon nextPokemon;

}
