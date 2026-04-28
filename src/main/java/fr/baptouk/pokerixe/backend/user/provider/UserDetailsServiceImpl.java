package fr.baptouk.pokerixe.backend.user.provider;

import fr.baptouk.pokerixe.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        User user = userRepository.findByMail(mail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + mail));

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getMail(),
                user.getPassword(),
                authorities
        );
    }
}