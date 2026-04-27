package fr.baptouk.pokerixe.backend.user.dto;

import lombok.Getter;

@Getter
public class UserResponseUpdateDTO {

    private final String pseudo, mail;

    public UserResponseUpdateDTO(String pseudo, String mail) {
        this.pseudo = pseudo;
        this.mail = mail;
    }
}
