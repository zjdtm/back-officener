package fastcampus.team7.Livable_officener.global.sercurity;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다 :" + username));

        return new CustomUserDetails(user);
    }

}
