package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.LoginDTO;
import fastcampus.team7.Livable_officener.dto.LoginDTO.LoginRequestDTO;
import fastcampus.team7.Livable_officener.global.exception.InvalidPasswordException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundUserException;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.global.util.RedisUtil;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @InjectMocks
    SignUpService signUpService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtProvider jwtProvider;

    @Mock
    RedisUtil redisUtil;

    @Test
    @DisplayName("로그인 요청시 회원을 찾을 수 없는 경우 '존재하지 않는 회원입니다.' 에러가 발생하는지 테스트")
    public void givenWrongEmailAndPassword_whenLoginRequest_thenExceptionNotFoundUser() {

        // given
        final String email = "test@gmail.com";
        given(userRepository.findByEmail(email)).willThrow(new NotFoundUserException());

        // when & then
        assertThrows(NotFoundUserException.class, () -> {
            signUpService.login(LoginRequestDTO.builder()
                    .email(email)
                    .password(anyString())
                    .build());
        });

    }

    @Test
    @DisplayName("로그인 요청시 패스워드가 일치하지 않을 경우 '패스워드가 일치하지 않습니다.' 에러가 발생하는지 테스트")
    public void givenEmailAndWrongPassword_whenLoginRequest_thenExceptionInvalidPassword() {

        // given
        final String email = "test@gmail.com";
        final String password = "test12!@";
        final String invalidPassword = "@!21tset";

        given(userRepository.findByEmail(email)).willReturn(
                Optional.of(User.builder()
                        .password(password)
                        .build()));
        given(passwordEncoder.matches(invalidPassword, password)).willReturn(false);

        // when & then
        assertThrows(InvalidPasswordException.class, () -> {
            signUpService.login(LoginRequestDTO.builder()
                    .email(email)
                    .password(invalidPassword)
                    .build());
        });

    }

    @Test
    @DisplayName("로그인 요청 성공시 회원의 정보, 건물, 오피스, 토큰을 정상적으로 응답받는지 테스트")
    public void givenEmailAndPassword_whenLoginRequest_thenReturnSuccess() {

        // given
        final String email = "test@gmail.com";
        final String name = "고길동";
        final String password = passwordEncoder.encode("고길동123");
        final String phoneNumber = "01054546789";
        Building building = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "356");
        Company company = saveCompanies(building, "테스트 오피스", "A동 101호");
        String token = jwtProvider.createToken(email);

        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .building(building)
                .company(company)
                .build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(password, user.getPassword())).willReturn(true);
        given(jwtProvider.createToken(email)).willReturn(token);

        // when
        LoginDTO result = signUpService.login(LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build());

        // then
        LoginDTO.LoginResponseDTO resultUserInfo = result.getUserInfo();
        assertThat(resultUserInfo.getEmail()).isEqualTo(email);
        assertThat(resultUserInfo.getBuilding().getBuildingName()).isEqualTo(building.getName());
        assertThat(resultUserInfo.getOffice().getOfficeName()).isEqualTo(company.getName());
        assertThat(resultUserInfo.getName()).isEqualTo(name);
        assertThat(resultUserInfo.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(resultUserInfo.getToken()).isEqualTo(token);

    }

    @Test
    @DisplayName("로그아웃 요청 성공시 정상적으로 로그아웃 되는지 테스트")
    public void givenToken_whenLogoutRequest_thenLogoutFailure() {

        // given
        final String email = "test@gmail.com";
        final String name = "고길동";
        final String password = passwordEncoder.encode("고길동123");
        final String phoneNumber = "01054546789";
        Building building = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "356");
        Company company = saveCompanies(building, "테스트 오피스", "A동 101호");

        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .building(building)
                .company(company)
                .build();

        String accessToken = jwtProvider.createToken(email);
        String bearerTokenPrefix = "fewpp3onpgonwpgnipngiwpip";

        given(jwtProvider.parseAccessToken("Bearer " + accessToken)).willReturn(bearerTokenPrefix);
        given(jwtProvider.getExpirationTime(bearerTokenPrefix)).willReturn(10L);

        // when
        signUpService.logout(user, "Bearer " + accessToken);

        // then
        verify(jwtProvider, times(1)).parseAccessToken("Bearer " + accessToken);
        verify(jwtProvider, times(1)).getExpirationTime(bearerTokenPrefix);
        verify(redisUtil, times(1)).setBlackList(bearerTokenPrefix, user.getEmail(), 10L);

    }

    private Company saveCompanies(Building building, String officeName, String officeUnit) {

        return Company
                .builder()
                .building(building)
                .name(officeName)
                .address(officeUnit)
                .build();

    }

    private Building saveBuilding(String name, String region, String city, String street, String zipcode) {

        return Building
                .builder()
                .name(name)
                .region(region)
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .build();

    }
}
