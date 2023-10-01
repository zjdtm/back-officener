package fastcampus.team7.Livable_officener.service;


import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.global.exception.*;
import fastcampus.team7.Livable_officener.global.util.RedisUtil;
import fastcampus.team7.Livable_officener.repository.BuildingRepository;
import fastcampus.team7.Livable_officener.repository.CompanyRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @InjectMocks
    SignUpService signUpService;

    @Mock
    CompanyRepository companyRepository;
    @Mock
    BuildingRepository buildingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RedisUtil redisUtil;

    @Test
    @DisplayName("키워드 입력시 건물명이 키워드에 포함되어 있는 경우 모든 건물과 회사의 정보들이 출력되는지 테스트")
    public void givenKeyword_whenSearch_thenReturnBuildingWithCompanies() {

        // given
        final String keyword = "미";
        List<Building> buildings = new ArrayList<>();
        Building building = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "364");
        buildings.add(building);

        List<Company> companies = new ArrayList<>();
        Company company1 = saveCompanies(building, "진회사", "A동 101호");
        Company company2 = saveCompanies(building, "칠리버블", "A동 102호");
        Company company3 = saveCompanies(building, "식스센스", "A동 103호");
        companies.add(company1);
        companies.add(company2);
        companies.add(company3);

        given(buildingRepository.findBuildingsByNameContaining(keyword)).willReturn(buildings);
        given(companyRepository.findCompaniesByBuildingName(buildings.get(0).getName())).willReturn(companies);

        // when
        Map<String, List<BuildingWithCompaniesDTO>> buildingWithCompanies = signUpService.getBuildingWithCompanies(keyword);

        // then
        BuildingWithCompaniesDTO expectedDto = buildingWithCompanies.get("buildings").get(0);
        List<CompanyDTO> companyDTOS = expectedDto.getOffices();

        assertThat(expectedDto.getBuildingName()).isEqualTo(building.getName());
        assertThat(expectedDto.getBuildingAddress()).isEqualTo(building.getRegion() + " " + building.getCity() + " " + building.getStreet() + " " + building.getZipcode());
        assertThat(companyDTOS.get(0).getOfficeName()).isEqualTo("진회사");
        assertThat(companyDTOS.get(1).getOfficeName()).isEqualTo("칠리버블");
        assertThat(companyDTOS.get(2).getOfficeName()).isEqualTo("식스센스");

        then(buildingRepository).should(times(1)).findBuildingsByNameContaining(keyword);
        then(companyRepository).should(times(1)).findCompaniesByBuildingName(buildings.get(0).getName());

    }

    @Test
    @DisplayName("키워드 입력시 건물명이 키워드에 포함되어 있는 경우를 찾지 못할 경우 빈 값이 출력되는지 테스트")
    public void givenKeyword_whenSearch_thenReturnEmptyBuilding() {

        // given
        final String keyword = "존재하지 않는 빌딩";

        given(buildingRepository.findBuildingsByNameContaining(keyword)).willReturn(Collections.emptyList());

        // when
        Map<String, List<BuildingWithCompaniesDTO>> buildingWithCompanies = signUpService.getBuildingWithCompanies(keyword);

        // then
        assertThat(buildingWithCompanies.get("buildings")).isEmpty();
    }

    @Test
    @DisplayName("휴대폰 인증번호 요청시 인증코드가 성공적으로 반환되는지 테스트")
    public void givenPhoneNumberAndName_whenRequest_thenReturnVerifyCode() {

        // given
        final String phoneNumber = "010-1234-5678";
        final String verifyCode = "982752";

        given(redisUtil.setPhoneAuthCode(phoneNumber)).willReturn(verifyCode);

        // when
        PhoneAuthResponseDTO result = signUpService.getPhoneAuthCode(PhoneAuthRequestDTO.builder()
                .name(anyString())
                .phoneNumber(phoneNumber)
                .build());

        // then
        verify(redisUtil, times(1)).setPhoneAuthCode(phoneNumber);
        assertThat(result.getVerifyCode()).isEqualTo(verifyCode);

    }

    @Test
    @DisplayName("휴대폰 인증번호 요청시 이미 등록된 휴대폰 번호일 경우 인증코드 업데이트 성공하는지 테스트")
    public void givenPhoneNumberAndName_whenRequest_thenUpdateVerifyCode() {

        // given
        final String phoneNumber = "010-1234-5678";
        final String verifyCode = "982752";
        final String changedVerifyCode = "876678";

        given(redisUtil.getPhoneAuthCode(phoneNumber)).willReturn(verifyCode);
        given(redisUtil.changePhoneAuthCode(phoneNumber)).willReturn(changedVerifyCode);

        // when
        PhoneAuthResponseDTO result = signUpService.getPhoneAuthCode(PhoneAuthRequestDTO.builder()
                .name(anyString())
                .phoneNumber(phoneNumber)
                .build());

        // then
        verify(redisUtil, times(1)).changePhoneAuthCode(phoneNumber);
        assertThat(result.getVerifyCode()).isNotEqualTo(verifyCode);
        assertThat(result.getVerifyCode()).isEqualTo(changedVerifyCode);

    }

    @Test
    @DisplayName("휴대폰 인증번호 요청시 이미 등록된 핸드폰 번호 일 경우 예외 발생")
    public void givenPhoneNumberAndName_whenRequest_thenExceptionDuplicatedPhoneNumber() {

        // given
        final String name = "고길동";
        final String phoneNumber = "01012345678";

        given(userRepository.existsByPhoneNumber(any(String.class))).willThrow(new DuplicatedPhoneNumberException());

        // when
        DuplicatedPhoneNumberException exception = assertThrows(DuplicatedPhoneNumberException.class, () -> {
            signUpService.getPhoneAuthCode(PhoneAuthRequestDTO.builder()
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .build());
        });

        // then
        assertThat(exception.getMessage()).isEqualTo(DuplicatedPhoneNumberException.DEFAULT_MESSAGE);

    }

    @Test
    @DisplayName("휴대폰 인증코드 확인 요청시 성공하는지 테스트")
    public void givenVerifyCode_whenConfirmRequest_thenConfirmSuccess() {

        // given
        final String phoneNumber = "01012345678";
        final String verifyCode = "135791";

        given(redisUtil.hasPhoneAuthCode(phoneNumber)).willReturn(true);
        given(redisUtil.getPhoneAuthCode(phoneNumber)).willReturn(verifyCode);

        PhoneAuthConfirmDTO phoneAuthConfirmDTO = PhoneAuthConfirmDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(verifyCode)
                .build();


        // when
        boolean isConfirm = signUpService.confirmVerifyCode(phoneAuthConfirmDTO);

        // then
        assertThat(isConfirm).isTrue();

    }

    @Test
    @DisplayName("휴대폰 인증코드 확인 요청시 휴대폰 번호로 요청한 적이 없는 경우 '인증되지 않은 핸드폰 번호입니다.' 예외 발생")
    public void givenVerifyCode_whenConfirmRequest_thenExceptionPhoneNumber() {

        // given
        final String phoneNumber = "01012345678";
        final String verifyCode = "135791";

        given(redisUtil.hasPhoneAuthCode(phoneNumber)).willReturn(false);

        PhoneAuthConfirmDTO phoneAuthConfirmDTO = PhoneAuthConfirmDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(verifyCode)
                .build();

        // when & then
        assertThrows(NotVerifiedPhoneNumberException.class, () -> {
            signUpService.confirmVerifyCode(phoneAuthConfirmDTO);
        });

    }

    @Test
    @DisplayName("휴대폰 인증코드 확인 요청시 검증에 실패하는 경우 '잘못된 인증 코드입니다.' 예외 발생")
    public void givenVerifyCode_whenConfirmRequest_thenNotVerifyCode() {

        // given
        final String phoneNumber = "01012345678";
        final String validVerifyCode = "999999";
        final String invalidVerifyCode = "135791";

        given(redisUtil.hasPhoneAuthCode(phoneNumber)).willReturn(true);
        given(redisUtil.getPhoneAuthCode(phoneNumber)).willReturn(validVerifyCode);

        PhoneAuthConfirmDTO phoneAuthConfirmFailedDTO = PhoneAuthConfirmDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(invalidVerifyCode)
                .build();

        // when & then
        assertThrows(NotVerifiedPhoneAuthCodeException.class, () -> {
            signUpService.confirmVerifyCode(phoneAuthConfirmFailedDTO);
        });
    }

    @Test
    @DisplayName("회원가입 요청시 회원가입에 성공하는지 테스트")
    public void givenUserInfo_whenRequest_thenSuccessSignUp() {

        // given
        final String email = "test@gmail.com";
        final String name = "고길동";
        final String password = passwordEncoder.encode("고길동123");
        final String phoneNumber = "01054546789";
        Building building = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "356");
        Company company = saveCompanies(building, "테스트 오피스", "A동 101호");

        given(buildingRepository.findByName(building.getName())).willReturn(Optional.of(building));
        given(companyRepository.findByName(company.getName())).willReturn(Optional.of(company));
        given(userRepository.existsByEmail(email)).willReturn(false);

        // when
        signUpService.signUp(SignUpRequestDTO.builder()
                .agree(true)
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .buildingName(building.getName())
                .companyName(company.getName())
                .build());

        // then
        verify(userRepository).save(any());

    }

    @Test
    @DisplayName("회원가입시 요청 Request 에 있는 건물명 이 존재하지 않는 건물일 경우 예외 발생")
    public void givenUserInfo_whenRequestUserInfo_thenNotFoundBuildingException() {

        // given
        final String email = "test@gmail.com";
        final String name = "고길동";
        final String password = passwordEncoder.encode("고길동123");
        final String phoneNumber = "01054546789";
        Building building = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "356");
        Company company = saveCompanies(building, "테스트 오피스", "A동 101호");

        given(buildingRepository.findByName(building.getName())).willThrow(NotFoundBuildingException.class);

        // when & then
        assertThrows(NotFoundBuildingException.class, () -> {
            signUpService.signUp(SignUpRequestDTO.builder()
                    .agree(true)
                    .name(name)
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .buildingName(building.getName())
                    .companyName(company.getName())
                    .build());
        });

    }

    @Test
    @DisplayName("회원가입시 요청 Request 에 있는 회사명 이 존재하지 않는 회사일 경우 예외 발생")
    public void givenUserInfo_whenRequestUserInfo_thenNotFoundCompanyException() {

        // given
        final String email = "test@gmail.com";
        final String name = "고길동";
        final String password = passwordEncoder.encode("고길동123");
        final String phoneNumber = "01054546789";
        Building building = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "356");
        Company company = saveCompanies(building, "테스트 오피스", "A동 101호");

        given(buildingRepository.findByName(building.getName())).willReturn(Optional.of(building));
        given(companyRepository.findByName(company.getName())).willThrow(NotFoundCompanyException.class);

        // when & then
        assertThrows(NotFoundCompanyException.class, () -> {
            signUpService.signUp(SignUpRequestDTO.builder()
                    .agree(true)
                    .name(name)
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .buildingName(building.getName())
                    .companyName(company.getName())
                    .build());
        });

    }

    @Test
    @DisplayName("회원가입시 이미 등록되어 있는 회원 이메일인 경우 예외 발생")
    public void givenUserInfo_whenRequestUserInfo_thenDuplicatedUserEmailException() {

        // given
        final String email = "test@gmail.com";
        final String name = "고길동";
        final String password = passwordEncoder.encode("고길동123");
        final String phoneNumber = "01054546789";
        Building building = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "356");
        Company company = saveCompanies(building, "테스트 오피스", "A동 101호");

        given(buildingRepository.findByName(building.getName())).willReturn(Optional.of(building));
        given(companyRepository.findByName(company.getName())).willReturn(Optional.of(company));
        given(userRepository.existsByEmail(email)).willThrow(DuplicatedUserEmailException.class);

        // when & then
        assertThrows(DuplicatedUserEmailException.class, () -> {
            signUpService.signUp(SignUpRequestDTO.builder()
                    .agree(true)
                    .name(name)
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .buildingName(building.getName())
                    .companyName(company.getName())
                    .build());
        });

    }

    @Test
    @DisplayName("회원가입시 이미 등록되어 있는 핸드폰 번호일 경우 예외 발생")
    public void givenUserInfo_whenRequestUserInfo_thenDuplicatedPhoneNumberException() {

        // given
        final String email = "test@gmail.com";
        final String name = "고길동";
        final String password = passwordEncoder.encode("고길동123");
        final String phoneNumber = "01054546789";
        Building building = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "356");
        Company company = saveCompanies(building, "테스트 오피스", "A동 101호");

        given(buildingRepository.findByName(building.getName())).willReturn(Optional.of(building));
        given(companyRepository.findByName(company.getName())).willReturn(Optional.of(company));
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByPhoneNumber(phoneNumber)).willThrow(DuplicatedPhoneNumberException.class);

        // when & then
        assertThrows(DuplicatedPhoneNumberException.class, () -> {
            signUpService.signUp(SignUpRequestDTO.builder()
                    .agree(true)
                    .name(name)
                    .email(email)
                    .password(password)
                    .phoneNumber(phoneNumber)
                    .buildingName(building.getName())
                    .companyName(company.getName())
                    .build());
        });

    }

    @Test
    @DisplayName("회원가입 요청시 정상적으로 저장되는지 테스트")
    public void givenUserInfo_whenRequestUserInfo_thenSuccess() {

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

        given(buildingRepository.findByName(building.getName())).willReturn(Optional.of(building));
        given(companyRepository.findByName(company.getName())).willReturn(Optional.of(company));
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByPhoneNumber(phoneNumber)).willReturn(false);

        // when
        signUpService.signUp(SignUpRequestDTO.builder()
                .agree(true)
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .buildingName(building.getName())
                .companyName(company.getName())
                .build());

        // then
        verify(userRepository).save(refEq(user));
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
