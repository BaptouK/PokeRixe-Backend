package fr.baptouk.pokerixe.backend.auth;

import fr.baptouk.pokerixe.backend.auth.dto.SignInRequest;
import fr.baptouk.pokerixe.backend.auth.dto.SignUpRequest;
import fr.baptouk.pokerixe.backend.auth.jwt.JwtService;
import fr.baptouk.pokerixe.backend.user.User;
import fr.baptouk.pokerixe.backend.user.provider.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public User signUp(SignUpRequest request) {
        if (userRepository.findByMail(request.getMail()).isPresent()) {
            throw new IllegalArgumentException("Mail already in use");
        }
        User user = new User(request.getMail(), passwordEncoder.encode(request.getPassword()), request.getPseudo());
        return userRepository.save(user);
    }

    public User signIn(SignInRequest request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getMail());
        String jwt = jwtService.generateToken(userDetails);

        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        return userRepository.findByMail(request.getMail()).orElseThrow();
    }

    public void signOut(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // TODO : true en prod
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}