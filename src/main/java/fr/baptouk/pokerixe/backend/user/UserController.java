package fr.baptouk.pokerixe.backend.user;

import fr.baptouk.pokerixe.backend.user.provider.UserNotFoundException;
import fr.baptouk.pokerixe.backend.user.provider.UserService;
import fr.baptouk.pokerixe.backend.user.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public @ResponseBody ResponseEntity<Iterable<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("{uuid}")
    public @ResponseBody ResponseEntity<User> getUser(@PathVariable final UUID uuid) {
        try {
            return ResponseEntity.ok(userService.getUser(uuid));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("team")
    public @ResponseBody ResponseEntity<User> editTeam(@RequestBody final Team team) {
        final User user = null; // TODO : Get user by token


        try {
            return ResponseEntity.ok(userService.editTeam(user, team));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("profile")
    public @ResponseBody ResponseEntity<User> editProfile(@RequestBody final String pseudo, final String mail) {
        final User user = null; // TODO : Get user by token

        return ResponseEntity.ok(userService.editProfile(user, pseudo, mail));
    }

}
