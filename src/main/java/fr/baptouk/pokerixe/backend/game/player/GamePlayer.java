package fr.baptouk.pokerixe.backend.game.player;

import fr.baptouk.pokerixe.backend.user.team.Team;
import fr.baptouk.pokerixe.backend.user.team.pokemon.Pokemon;

import java.util.UUID;

public final class GamePlayer {

    private UUID id;

    private String pseudo;

    private Team team;

    private Pokemon selectedPokemon;

}
