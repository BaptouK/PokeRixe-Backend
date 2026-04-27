package fr.baptouk.pokerixe.backend.auth;

import fr.baptouk.pokerixe.backend.auth.dto.SignInRequest;
import fr.baptouk.pokerixe.backend.auth.dto.SignUpRequest;
import fr.baptouk.pokerixe.backend.shared.ApiResponse;
import fr.baptouk.pokerixe.backend.user.dto.UserResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signIn(@RequestBody SignInRequest request,
                                                               HttpServletResponse response) {
        return ResponseEntity.ok(authService.signIn(request, response));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDTO>> signUp(@RequestBody SignUpRequest request) {
        return ResponseEntity.ok(authService.signUp(request));
    }

    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(HttpServletResponse response) {
        authService.signOut(response);
        return ResponseEntity.ok().build();
    }
}