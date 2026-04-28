package fr.baptouk.pokerixe.backend.auth;

import fr.baptouk.pokerixe.backend.user.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<User> signIn(@RequestParam String password, @RequestParam String mail,
                                       HttpServletResponse response) {
        return ResponseEntity.ok(authService.signIn(password,mail,response));
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestParam String pseudo, @RequestParam String password, @RequestParam String mail) {
        return ResponseEntity.ok(authService.signUp(pseudo,password,mail));
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(HttpServletResponse response) {
        authService.signOut(response);
        return ResponseEntity.ok().build();
    }
}