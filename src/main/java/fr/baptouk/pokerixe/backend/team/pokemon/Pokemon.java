package fr.baptouk.pokerixe.backend.team.pokemon;

import fr.baptouk.pokerixe.backend.team.pokemon.attacks.Attack;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@AllArgsConstructor
@Data
public class Pokemon {

    private int id;
    private String apiUrl;

    private List<Attack> attacks;

}
