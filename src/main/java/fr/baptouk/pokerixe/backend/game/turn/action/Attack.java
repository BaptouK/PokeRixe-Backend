package fr.baptouk.pokerixe.backend.game.turn.action;

import fr.baptouk.pokerixe.backend.user.team.pokemon.Pokemon;

public final class Attack extends Action{

    public Attack() {
        super("Attaque");
    }

    private String apiUrl;

    private Pokemon attacker;

    private Pokemon target;
}
