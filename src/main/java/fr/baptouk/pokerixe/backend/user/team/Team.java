package fr.baptouk.pokerixe.backend.user.team;

import fr.baptouk.pokerixe.backend.user.team.pokemon.Pokemon;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public final class Team {

    private UUID id = UUID.randomUUID();
    private List<Pokemon> pokemons;

}
