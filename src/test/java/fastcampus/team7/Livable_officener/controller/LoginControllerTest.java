package fastcampus.team7.Livable_officener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.login.LoginDTO;
import fastcampus.team7.Livable_officener.dto.login.LoginDTO.LoginRequestDTO;
import fastcampus.team7.Livable_officener.global.exception.InvalidPasswordException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundUserException;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import fastcampus.team7.Livable_officener.service.SignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static fastcampus.team7.Livable_officener.dto.signup.BuildingWithCompaniesDTO.BuildingResponseDTO;
import static fastcampus.team7.Livable_officener.dto.signup.BuildingWithCompaniesDTO.BuildingWithCompaniesResponseDTO.CompanyResponseDTO;
import static fastcampus.team7.Livable_officener.dto.login.LoginDTO.LoginResponseDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignUpController.class)
@MockBean(JpaMetamodelMappingContext.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    SignUpService signUpService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    JwtProvider jwtProvider;

    @Test
    @WithMockUser
    @DisplayName("로그인 요청시 이메일 유효성 검증에 실패할 경우 '유효하지 않은 이메일 주소입니다.' 에러 발생")
    void givenInvalidEmailAndPassword_whenLoginRequest_thenNotValidEmail() throws Exception {

        // given
        final String email = "1111111";
        final String password = "test12#$";

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        // when & then
        mvc.perform(post("/api/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("유효하지 않은 이메일 주소입니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 요청시 비밀번호 유효성 검증에 실패할 경우 '비밀번호는 영문, 숫자, 특수기호 를 포함한 8 ~ 16자입니다.' 에러 발생")
    void givenEmailAndWrongPassword_whenLoginRequest_thenNotValidPassword() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "1111111111111111";

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        // when & then
        mvc.perform(post("/api/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("비밀번호는 영문, 숫자, 특수기호 를 포함한 8 ~ 16자입니다."));

    }

    @Test
    @WithMockUser
    @DisplayName("로그인 요청시 가입된 회원이 아닐 경우 '존재하지 않는 회원입니다.' 에러 발생")
    void givenWrongEmailAndPassword_whenLoginRequest_thenLoginFailure() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "Test12!@";

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        given(signUpService.login(any(LoginRequestDTO.class))).willThrow(new NotFoundUserException());

        // when & then
        mvc.perform(post("/api/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(NotFoundUserException.DEFAULT_MESSAGE));
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 요청시 비밀번호가 일치하지 않을 경우 '패스워드가 일치하지 않습니다.' 에러 발생")
    void givenEmailAndWrongPassword_whenLoginRequest_thenLoginFailure() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "Test12!@";

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        given(signUpService.login(any(LoginRequestDTO.class))).willThrow(new InvalidPasswordException());

        // when & then
        mvc.perform(post("/api/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(InvalidPasswordException.DEFAULT_MESSAGE));
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 요청 성공시 회원, 건물, 오피스, 토큰의 정보가 응답값으로 들어오는지 테스트")
    void givenEmailAndPassword_whenLoginRequest_thenLoginSuccess() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "Test12!@";
        final String name = "고길동";
        final String phoneNumber = "01012345678";
        String token = jwtProvider.createToken(email);
        BuildingResponseDTO buildingResponseDTO = BuildingResponseDTO.builder()
                .id(1L)
                .buildingName("테스트 빌딩")
                .buildingAddress("서울 강남구 강남대로 364")
                .build();
        CompanyResponseDTO companyResponseDTO = CompanyResponseDTO.builder()
                .id(1L)
                .officeName("테스트 오피스")
                .officeNum("A동 101호")
                .build();

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .email(email)
                .password(password)
                .build();

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .id(1L)
                .email(email)
                .name(name)
                .phoneNumber(phoneNumber)
                .building(buildingResponseDTO)
                .office(companyResponseDTO)
                .token(token)
                .build();

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUserInfo(loginResponseDTO);

        given(signUpService.login(any(LoginRequestDTO.class))).willReturn(loginDTO);

        // when & then
        mvc.perform(post("/api/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.userInfo.email").value(email))
                .andExpect(jsonPath("$.data.userInfo.name").value(name))
                .andExpect(jsonPath("$.data.userInfo.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.data.userInfo.building.buildingName").value(buildingResponseDTO.getBuildingName()))
                .andExpect(jsonPath("$.data.userInfo.office.officeName").value(companyResponseDTO.getOfficeName()))
                .andExpect(jsonPath("$.data.userInfo.token").value(token));
    }

    @Test
    @WithMockUser
    @DisplayName("로그아웃 요청 성공시 응답값으로 '로그아웃에 성공했습니다.' 를 받는지 테스트")
    void givenAccessToken_whenLogoutRequest_thenLogoutSuccess() throws Exception {

        // given
        doNothing().when(signUpService).logout(any(User.class), anyString());

        // when & then
        mvc.perform(post("/api/logout")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + jwtProvider.createToken(anyString())
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("로그아웃에 성공하였습니다."));
    }

}
