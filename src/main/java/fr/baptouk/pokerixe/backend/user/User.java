package fr.baptouk.pokerixe.backend.user;

import fr.baptouk.pokerixe.backend.user.team.Team;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Document(collection = "users")
@Data
public final class User {

    @Id
    private UUID id = UUID.randomUUID();

    private String mail, password, pseudo;

    private Team team;

    private List<String> roles = List.of("ROLE_USER"); // Pour spring Security

    public User(String mail, String password, String pseudo) {
        this.mail = mail;
        this.password = password;
        this.pseudo = pseudo;
    }
}
