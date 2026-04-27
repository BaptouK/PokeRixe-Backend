package fr.baptouk.pokerixe.backend.shared;

import lombok.Data;

import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
}