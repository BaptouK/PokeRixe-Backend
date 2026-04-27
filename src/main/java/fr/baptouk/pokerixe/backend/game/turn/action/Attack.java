package fr.baptouk.pokerixe.backend.game.turn.action;

import fr.baptouk.pokerixe.backend.game.gameplayer.GamePlayer;
import fr.baptouk.pokerixe.backend.team.pokemon.Pokemon;

public class Attack extends Action{

    public Attack() {
        super("Attaque");
    }

    private String apiUrl;

    private Pokemon attacker;

    private Pokemon target;
}
