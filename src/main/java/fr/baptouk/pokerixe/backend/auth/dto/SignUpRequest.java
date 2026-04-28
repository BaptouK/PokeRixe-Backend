package fr.baptouk.pokerixe.backend.auth.dto;

import lombok.Data;

@Data
public class SignUpRequest {
    private String mail;
    private String password;
    private String pseudo;
}