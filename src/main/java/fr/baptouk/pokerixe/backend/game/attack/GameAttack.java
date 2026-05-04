package fr.baptouk.pokerixe.backend.game.attack;

import fr.baptouk.pokerixe.backend.game.pokemon.PokemonType;
import fr.baptouk.pokerixe.backend.game.provider.dto.PokeApiMoveDto;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Data
public class GameAttack {
    private UUID id = UUID.randomUUID();
    private String name;
    private Integer power;
    private PokemonType type;
    private Integer accuracy;
    private DamageClass powerClass;
    private transient String apiUrl;


    public Mono<Void> fetch(WebClient pokeApiClient, String apiUrl) {
        return pokeApiClient.get()
                .uri(apiUrl)
                .retrieve()
                .bodyToMono(PokeApiMoveDto.class)
                .doOnNext(this::mapFromApi)
                .then();
    }

    private void mapFromApi(PokeApiMoveDto data) {
        this.name = data.names().stream()
                .filter(n -> n.language().name().equals("fr"))
                .map(PokeApiMoveDto.NameEntry::name)
                .findFirst()
                .orElse(data.name());

        this.power = data.power();
        this.accuracy = data.accuracy();

        this.type = PokemonType.valueOf(data.type().name().toUpperCase());

        this.powerClass = switch (data.damageClass().name()) {
            case "physical" -> DamageClass.PHYSICAL;
            case "special"  -> DamageClass.SPECIAL;
            default         -> DamageClass.PHYSICAL;
        };
    }
}