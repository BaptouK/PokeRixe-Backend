package fr.baptouk.pokerixe.backend.user.provider;

import fr.baptouk.pokerixe.backend.user.User;
import fr.baptouk.pokerixe.backend.user.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public final class UserService {

    @Autowired
    private UserRepository userRepository;

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUser(final UUID userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public User editTeam(final User user, final Team team) throws IllegalArgumentException {
        if (team.getPokemons().size() > 6){
            throw new IllegalArgumentException("A team cannot have more than 6 pokemons");
        }

        user.setTeam(team);
        return userRepository.save(user);
    }

    public User getByMail(String mail) throws UserNotFoundException {
        return userRepository.findByMail(mail)
                .orElseThrow(UserNotFoundException::new);
    }

    public User editProfile(final User user, String pseudo, String mail){
        user.setPseudo(pseudo.length() > 32 ? pseudo.substring(0, 32) : pseudo);
        user.setMail(mail);
        return userRepository.save(user);
    }

}
