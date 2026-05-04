package fr.baptouk.pokerixe.backend.game.pokemon;

import fr.baptouk.pokerixe.backend.game.attack.GameAttack;
import fr.baptouk.pokerixe.backend.game.provider.dto.PokeApiPokemonDto;
import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Data
public class GamePokemon {
    private UUID id = UUID.randomUUID();
    private Integer pokemonId;
    private String name;
    private PokemonStats stats;
    private Integer hp;
    private List<PokemonType> type = Collections.emptyList();
    private List<GameAttack> attacks = Collections.emptyList();
    private List<PokemonType> superEffectivesTypes  = Collections.emptyList();
    private List<PokemonType> notVeryEffective= Collections.emptyList();
    private List<PokemonType> noDamage = Collections.emptyList();
    private String urlImageFront;
    private String urlImageBack;

    public Mono<Void> fetch(WebClient pokeApiClient) {
        Mono<Void> pokemonFetch = pokeApiClient.get()
                .uri("/pokemon/{id}", this.pokemonId)
                .retrieve()
                .bodyToMono(PokeApiPokemonDto.class)
                .doOnNext(this::mapFromApi)
                .then();

        List<Mono<Void>> attackFetches = this.attacks.stream()
                .map(attack -> attack.fetch(pokeApiClient, attack.getApiUrl()))
                .toList();

        return Mono.when(pokemonFetch, Mono.when(attackFetches));
    }

    private void mapFromApi(PokeApiPokemonDto data) {
        this.name = data.name();
        this.urlImageFront = data.sprites().frontDefault();
        this.urlImageBack = data.sprites().backDefault();
        this.type = data.types().stream()
                .map(slot -> PokemonType.valueOf(slot.type().name().toUpperCase()))
                .toList();
        this.stats = mapStats(data.stats());
    }

    private PokemonStats mapStats(List<PokeApiPokemonDto.StatSlot> slots) {
        int hp  = extractStat(slots, "hp");
        int attack = extractStat(slots, "attack");
        int defense = extractStat(slots, "defense");
        int specialAttack = extractStat(slots, "special-attack");
        int specialDefense = extractStat(slots, "special-defense");
        int speed = extractStat(slots, "speed");
        this.hp = hp;
        return new PokemonStats(hp, attack,defense,specialAttack,specialDefense,speed);
    }

    private int extractStat(List<PokeApiPokemonDto.StatSlot> slots, String statName) {
        return slots.stream()
                .filter(s -> s.stat().name().equals(statName))
                .mapToInt(PokeApiPokemonDto.StatSlot::baseStat)
                .findFirst()
                .orElse(0);
    }
}