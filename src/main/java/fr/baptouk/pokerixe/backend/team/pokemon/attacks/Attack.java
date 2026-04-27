package fr.baptouk.pokerixe.backend.team.pokemon.attacks;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@Data
public class Attack {

    private int id;
    private String apiUrl;

}
