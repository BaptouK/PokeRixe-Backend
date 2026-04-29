package fr.baptouk.pokerixe.backend.user.dto;

import fr.baptouk.pokerixe.backend.user.User;
import fr.baptouk.pokerixe.backend.user.team.Team;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    //private final String id;
    private final String pseudo;
    private final String mail;
    private final int role;
    private final Team team;

    public UserResponseDTO(User user) {
        //this.id = user.getId().toString();
        this.pseudo = user.getPseudo();
        this.mail = user.getMail();
        this.role = user.getRoles().contains("ROLE_ADMIN") ? 0 : 1;
        this.team = user.getTeam();
    }
}
