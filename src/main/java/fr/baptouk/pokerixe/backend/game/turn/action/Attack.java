package fr.baptouk.pokerixe.backend.game.turn.action;

import fr.baptouk.pokerixe.backend.game.player.GamePlayer;
import lombok.Getter;

public final class Attack extends Action{

    public Attack(int indexAttackPokemon) {
        super("attack","Il a attaqué");
        this.indexAttack = indexAttackPokemon;
    }

    @Getter
    private final int indexAttack;

}
