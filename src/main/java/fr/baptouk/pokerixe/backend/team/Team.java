package fr.baptouk.pokerixe.backend.team;

import fr.baptouk.pokerixe.backend.team.pokemon.Pokemon;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "teams")
public class Team {

    private UUID id;
    private List<Pokemon> pokemons;

}
