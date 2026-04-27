package fr.baptouk.pokerixe.backend.user;

import fr.baptouk.pokerixe.backend.team.Team;
import fr.baptouk.pokerixe.backend.team.pokemon.Pokemon;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "users")
public class User {

    @Id
    private UUID id;

    private String mail, password, pseudo;

    // TODO : Historique
    private Team team;

}
