package fr.baptouk.pokerixe.backend.user.team.pokemon;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@Data
public final class Attack {

    private int id;
    private String apiUrl;

}
