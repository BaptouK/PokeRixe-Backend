package fr.baptouk.pokerixe.backend.game.gameplayer;

import fr.baptouk.pokerixe.backend.team.Team;
import fr.baptouk.pokerixe.backend.team.pokemon.Pokemon;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public class GamePlayer {

    @Id
    private UUID id;

    private String pseudo;

    private Team team;

    private Pokemon selectedpokemon;

}
