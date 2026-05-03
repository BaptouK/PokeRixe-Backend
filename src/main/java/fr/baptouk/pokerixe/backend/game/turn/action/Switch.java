package fr.baptouk.pokerixe.backend.game.turn.action;

import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import lombok.Getter;

public final class Switch extends Action{

    public Switch(int indexSwitchPokemon) {
        super("switch","Il a switch");
        this.indexPokemon = indexSwitchPokemon;
    }

    @Getter
    private final int indexPokemon;

}
