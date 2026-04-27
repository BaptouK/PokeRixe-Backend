package fr.baptouk.pokerixe.backend.game.provider;

import fr.baptouk.pokerixe.backend.game.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface GameRepository extends MongoRepository<Game, UUID> {
}
