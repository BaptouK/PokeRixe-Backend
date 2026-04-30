package fr.baptouk.pokerixe.backend.user;

import fr.baptouk.pokerixe.backend.user.dto.UserResponseDTO;
import fr.baptouk.pokerixe.backend.user.provider.UserNotFoundException;
import fr.baptouk.pokerixe.backend.user.provider.UserService;
import fr.baptouk.pokerixe.backend.user.team.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public final class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public @ResponseBody ResponseEntity<Iterable<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(new UserResponseDTO(userService.getByMail(userDetails.getUsername())));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("{uuid}")
    public @ResponseBody ResponseEntity<UserResponseDTO> getUser(@PathVariable final UUID uuid) {
        try {
            return ResponseEntity.ok(new UserResponseDTO(userService.getUser(uuid)));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("team")
    public @ResponseBody ResponseEntity<UserResponseDTO> editTeam(@AuthenticationPrincipal UserDetails userDetails,@RequestBody final Team team) {

        final User user = userService.getUserByToken(userDetails);

        try {
            return ResponseEntity.ok(new UserResponseDTO(userService.editTeam(user, team)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("profile")
    public @ResponseBody ResponseEntity<UserResponseDTO> editProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                          @RequestParam final String pseudo,
                                                          @RequestParam final String mail) {

        final User user = userService.getUserByToken(userDetails);

        return ResponseEntity.ok(new UserResponseDTO(userService.editProfile(user, pseudo, mail)));
    }

}
