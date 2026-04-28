package fr.baptouk.pokerixe.backend.auth.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String mail;
    private String password;
}
