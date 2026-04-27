package fr.baptouk.pokerixe.backend.team;

import fr.baptouk.pokerixe.backend.team.pokemon.Pokemon;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
public class Team {

    private UUID id = UUID.randomUUID();
    private List<Pokemon> pokemons;

}
