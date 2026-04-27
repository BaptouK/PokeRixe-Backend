package fr.baptouk.pokerixe.backend.user.provider;

import fr.baptouk.pokerixe.backend.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface UserRepository extends MongoRepository<User, UUID> {



}
