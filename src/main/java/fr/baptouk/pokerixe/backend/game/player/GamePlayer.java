package fr.baptouk.pokerixe.backend.game.player;

import fr.baptouk.pokerixe.backend.game.team.GameTeam;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public final class GamePlayer {

    private UUID id;

    private String pseudo;

    private GameTeam team ;

    private Integer indexSelectedPokemon;

    @Builder.Default
    private PlayerStatus status = PlayerStatus.WAITING_ACTION;

    private transient String sessionId;

    public Mono<Void> init(WebClient pokeApiClient) {
        List<Mono<Void>> tasks = this.team.getPokemons().stream()
                .map(pokemon -> pokemon.fetch(pokeApiClient))
                .toList();

        return Mono.when(tasks);
    }

}
