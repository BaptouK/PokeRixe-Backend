package fr.baptouk.pokerixe.backend.auth;

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

    public User signUp(String pseudo, String password, String mail) {
        if (userRepository.findByMail(mail).isPresent()) {
            throw new IllegalArgumentException("Mail already in use");
        }
        User user = new User(mail, passwordEncoder.encode(password), pseudo);
        return userRepository.save(user);
    }

    public User signIn(String password, String mail, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(mail, password)
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(mail);
        String jwt = jwtService.generateToken(userDetails);

        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);

        return userRepository.findByMail(mail).orElseThrow();
    }

    public void signOut(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true en prod
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}