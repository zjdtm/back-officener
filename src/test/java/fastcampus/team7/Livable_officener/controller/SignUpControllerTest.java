package fastcampus.team7.Livable_officener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.global.exception.DuplicatedPhoneNumberException;
import fastcampus.team7.Livable_officener.global.exception.NotVerifiedPhoneAuthCodeException;
import fastcampus.team7.Livable_officener.global.exception.NotVerifiedPhoneNumberException;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
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

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SignUpController.class)
@MockBean(JpaMetamodelMappingContext.class)
class SignUpControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SignUpService signUpService;

    @MockBean
    private JwtProvider jwtProvider;


    @Test
    @WithMockUser
    @DisplayName("건물 검색 키워드를 요청시 키워드에 포함된 건물과 오피스 정보들이 성공적으로 출력되는지 테스트")
    void givenKeyword_whenBuildingKeywordRequest_thenReturnBuildingWithCompanies() throws Exception {

        // given
        final String keyword = "미";
        Map<String, List<BuildingWithCompaniesDTO>> response = new HashMap<>();
        List<BuildingWithCompaniesDTO> buildingWithCompaniesDTOList = new ArrayList<>();
        BuildingWithCompaniesDTO buildingWithCompaniesDTO = BuildingWithCompaniesDTO
                .builder()
                .id(1L)
                .buildingName("미왕 빌딩")
                .buildingAddress("서울 강남구 강남대로 364")
                .build();

        List<CompanyDTO> companyDTOS = new ArrayList<>();
        companyDTOS.add(
                CompanyDTO.builder()
                        .id(1L)
                        .officeName("진회사")
                        .officeNum("A동 101호")
                        .build());
        companyDTOS.add(
                CompanyDTO.builder()
                        .id(2L)
                        .officeName("칠리버블")
                        .officeNum("A동 102호")
                        .build());
        companyDTOS.add(
                CompanyDTO.builder()
                        .id(3L)
                        .officeName("식스센스")
                        .officeNum("A동 103호")
                        .build());

        buildingWithCompaniesDTO.setOffices(companyDTOS);
        buildingWithCompaniesDTOList.add(buildingWithCompaniesDTO);
        response.put("buildings", buildingWithCompaniesDTOList);

        // when
        when(signUpService.getBuildingWithCompanies(keyword)).thenReturn(response);

        // then
        mvc.perform(get("/api/building").param("name", keyword))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.buildings", hasSize(1)))
                .andExpect(jsonPath("$.data.buildings[0].buildingName", is("미왕 빌딩")))
                .andExpect(jsonPath("$.data.buildings[0].buildingAddress", is("서울 강남구 강남대로 364")))
                .andExpect(jsonPath("$.data.buildings[0].offices", hasSize(3)))
                .andExpect(jsonPath("$.data.buildings[0].offices[0].officeName", is("진회사")))
                .andExpect(jsonPath("$.data.buildings[0].offices[1].officeName", is("칠리버블")))
                .andExpect(jsonPath("$.data.buildings[0].offices[2].officeName", is("식스센스")))
                .andReturn();

    }


    @Test
    @WithMockUser
    @DisplayName("건물 검색 키워드를 요청시 키워드에 포함된 건물이 없는 경우 빈 값이 출력되는지 테스트")
    void givenKeyword_whenBuildingKeywordRequest_thenReturnEmptyList() throws Exception {

        // given
        final String keyword = "없는 빌딩";
        Map<String, List<BuildingWithCompaniesDTO>> response = new HashMap<>();
        response.put("buildings", Collections.emptyList());

        // when
        when(signUpService.getBuildingWithCompanies(keyword)).thenReturn(response);

        // then
        mvc.perform(get("/api/building").param("name", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.buildings", hasSize(0)));
    }

    @Test
    @WithMockUser
    @DisplayName("휴대폰 번호 인증 요청시 이미 등록되어 있는 핸드폰 번호일 경우 '이미 등록된 휴대폰 번호입니다.' 예외 발생")
    void givenNameAndPhoneNumber_whenRequest_thenExceptionDuplicatedPhoneNumber() throws Exception {

        // given
        PhoneAuthRequestDTO phoneAuthRequestDTO = PhoneAuthRequestDTO.builder()
                .name("고길동")
                .phoneNumber("01012345678")
                .build();

        given(signUpService.getPhoneAuthCode(any(PhoneAuthRequestDTO.class))).willThrow(new DuplicatedPhoneNumberException());

        // when & then
        mvc.perform(post("/api/auth")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(phoneAuthRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(DuplicatedPhoneNumberException.DEFAULT_MESSAGE));
    }

    @Test
    @WithMockUser
    @DisplayName("휴대폰 번호 인증 요청시 인증코드를 성공적으로 응답받는지 테스트")
    void givenNameAndPhoneNumber_whenRequest_thenReturnSuccessPhoneAuthCode() throws Exception {

        // given
        final String verifyCode = "864583";
        PhoneAuthRequestDTO phoneAuthRequestDTO = PhoneAuthRequestDTO.builder()
                .name("고길동")
                .phoneNumber("01012345678")
                .build();

        PhoneAuthResponseDTO phoneAuthResponseDTO = PhoneAuthResponseDTO.builder()
                .verifyCode(verifyCode)
                .build();

        given(signUpService.getPhoneAuthCode(any(PhoneAuthRequestDTO.class))).willReturn(phoneAuthResponseDTO);

        // when & then
        mvc.perform(post("/api/auth")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(phoneAuthRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.verifyCode").value(verifyCode));
    }

    @Test
    @WithMockUser
    @DisplayName("휴대폰 인증코드 확인 요청시 인증요청을 한 적이 없는 핸드폰 번호일 경우 '인증되지 않은 핸드폰 번호입니다.' 예외 발생")
    void givenPhoneNumberAndVerifyCode_whenRequest_thenExceptionNotVerifyPhoneNumber() throws Exception {

        // given
        final String phoneNumber = "01056748825";
        final String verifyCode = "839728";
        PhoneAuthConfirmDTO phoneAuthConfirmDTO = PhoneAuthConfirmDTO.builder()
                .verifyCode(verifyCode)
                .phoneNumber(phoneNumber)
                .build();

        given(signUpService.confirmVerifyCode(any(PhoneAuthConfirmDTO.class))).willThrow(new NotVerifiedPhoneNumberException());

        // when & then
        mvc.perform(post("/api/confirm")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(phoneAuthConfirmDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value(NotVerifiedPhoneNumberException.DEFAULT_MESSAGE));
    }

    @Test
    @WithMockUser
    @DisplayName("휴대폰 인증코드 확인 요청시 검증에 실패하는 경우 '잘못된 인증 코드입니다.' 에러 메시지 출력")
    void givenPhoneNumberAndVerifyCode_whenRequest_thenExceptionNotVerifiedPhoneAuthCode() throws Exception {

        // given
        final String phoneNumber = "01056748825";
        final String verifyCode = "839728";
        PhoneAuthConfirmDTO phoneAuthConfirmDTO = PhoneAuthConfirmDTO.builder()
                .verifyCode(verifyCode)
                .phoneNumber(phoneNumber)
                .build();

        given(signUpService.confirmVerifyCode(any(PhoneAuthConfirmDTO.class))).willThrow(new NotVerifiedPhoneAuthCodeException());

        // when & then
        mvc.perform(post("/api/confirm")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(phoneAuthConfirmDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 인증 코드입니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("휴대폰 인증코드 확인 요청시 성공 테스트")
    void givenPhoneNumberAndVerifyCode_whenRequest_thenSuccess() throws Exception {

        // given
        final String phoneNumber = "01056748825";
        final String verifyCode = "839728";
        PhoneAuthConfirmDTO phoneAuthConfirmDTO = PhoneAuthConfirmDTO.builder()
                .verifyCode(verifyCode)
                .phoneNumber(phoneNumber)
                .build();

        given(signUpService.confirmVerifyCode(any(PhoneAuthConfirmDTO.class))).willReturn(true);

        // when & then
        mvc.perform(post("/api/confirm")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(phoneAuthConfirmDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("인증이 완료되었습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 요청시 비밀번호 유효성 검증에 실패할 경우 '비밀번호는 최소 8자 이상 15자 이하, 대문자, 소문자, 숫자, 특수 문자($@$!%*?&)를 포함해야 합니다.' 에러 메시지 출력")
    void givenUserInfo_whenRequest_thenPasswordValidationFailure() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "test";             // 비밀번호 유효성 검증에 맞지 않는 경우
        final String buildingName = "미왕 빌딩";
        final String companyName = "칠리버블";
        final String name = "테스트 유저";
        final String phoneNumber = "01012345678";
        final boolean agree = true;

        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email(email)
                .agree(agree)
                .password(password)
                .buildingName(buildingName)
                .companyName(companyName)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();

        // when & then
        mvc.perform(post("/api/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("비밀번호는 영문, 숫자, 특수기호 를 포함한 8 ~ 16자입니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 요청시 이메일 유효성 검증에 실패할 경우 '유효하지 않은 이메일 주소입니다.' 에러 메시지 출력")
    void givenUserInfo_whenRequest_thenEmailValidationFailure() throws Exception {

        // given
        final String email = "testgmail.com";           // @ 를 포함시키지 않았을 경우
        final String password = "Test12!@";
        final String buildingName = "미왕 빌딩";
        final String companyName = "칠리버블";
        final String name = "테스트 유저";
        final String phoneNumber = "01012345678";
        final boolean agree = true;

        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email(email)
                .agree(agree)
                .password(password)
                .buildingName(buildingName)
                .companyName(companyName)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();

        // when & then
        mvc.perform(post("/api/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("유효하지 않은 이메일 주소입니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 요청시 건물이 존재하지 않을 경우 '건물 명을 입력해주세요' 에러 메시지 출력")
    void givenUserInfo_whenRequest_thenNotFoundBuilding() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "Test12!@";
        final String buildingName = "";             // 건물이 없는 경우
        final String companyName = "칠리버블";
        final String name = "테스트 유저";
        final String phoneNumber = "01012345678";
        final boolean agree = true;

        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email(email)
                .agree(agree)
                .password(password)
                .buildingName(buildingName)
                .companyName(companyName)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();

        // when & then
        mvc.perform(post("/api/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("건물 명을 입력해주세요"));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 요청시 회사명이 없을 경우 '회사 명을 입력해주세요' 에러 메시지 출력")
    void givenUserInfo_whenRequest_thenNotFoundCompany() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "Test12!@";
        final String buildingName = "미왕 빌딩";
        final String companyName = "";                  // 회사가 없는 경우
        final String name = "테스트 유저";
        final String phoneNumber = "01012345678";
        final boolean agree = true;

        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email(email)
                .agree(agree)
                .password(password)
                .buildingName(buildingName)
                .companyName(companyName)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();

        // when & then
        mvc.perform(post("/api/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("회사 명을 입력해주세요"));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 요청시 이름이 비어있을 경우 '이름을 입력해주세요' 에러 메시지 출력")
    void givenUserInfo_whenRequest_thenNotEmptyName() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "Test12!@";
        final String buildingName = "미왕 빌딩";
        final String companyName = "칠리버블";
        final String name = "";                         // 이름이 없는 경우
        final String phoneNumber = "01012345678";
        final boolean agree = true;

        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email(email)
                .agree(agree)
                .password(password)
                .buildingName(buildingName)
                .companyName(companyName)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();

        // when & then
        mvc.perform(post("/api/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("이름을 입력해주세요"));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 요청시 핸드폰번호가 비어있을 경우 '핸드폰 번호는 숫자만 입력해주세요' 에러 메시지 출력")
    void givenUserInfo_whenRequest_thenNotEmptyPhoneNumber() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "Test12!@";
        final String buildingName = "미왕 빌딩";
        final String companyName = "칠리버블";
        final String name = "고길동";
        final String phoneNumber = "";              // 핸드폰 번호가 비어 있음
        final boolean agree = true;

        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email(email)
                .agree(agree)
                .password(password)
                .buildingName(buildingName)
                .companyName(companyName)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();

        // when & then
        mvc.perform(post("/api/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.errorMessage").value("핸드폰 번호는 숫자만 입력해주세요"));
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 요청시 성공하는지 테스트")
    void givenUserInfo_whenRequest_thenSignupSuccess() throws Exception {

        // given
        final String email = "test@gmail.com";
        final String password = "Test12!@";
        final String buildingName = "미왕 빌딩";
        final String companyName = "칠리버블";
        final String name = "고길동";
        final String phoneNumber = "01012345678";
        final boolean agree = true;

        SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                .email(email)
                .agree(agree)
                .password(password)
                .buildingName(buildingName)
                .companyName(companyName)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();

        // when & then
        mvc.perform(post("/api/signup")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(signUpRequestDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("회원가입에 성공했습니다."));
    }

}
