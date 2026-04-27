package fr.baptouk.pokerixe.backend.user.team.pokemon;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@AllArgsConstructor
@Data
public final class Pokemon {

    private int id;
    private String apiUrl;

    private List<Attack> attacks;

}
