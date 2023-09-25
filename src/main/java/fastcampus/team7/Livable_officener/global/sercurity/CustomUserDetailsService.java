package fastcampus.team7.Livable_officener.global.sercurity;

import fastcampus.team7.Livable_officener.global.exception.NotFoundUserException;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundUserException(username));
    }

}
