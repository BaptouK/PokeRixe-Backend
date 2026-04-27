package fr.baptouk.pokerixe.backend.user.dto;

import fr.baptouk.pokerixe.backend.user.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private final String id;
    private final String name;
    private final String mail;
    private final int role;

    public UserResponseDTO(User user) {
        this.id = user.getId().toString();
        this.name = user.getPseudo();
        this.mail = user.getMail();
        this.role = user.getRoles().contains("ROLE_ADMIN") ? 0 : 1;
    }
}
