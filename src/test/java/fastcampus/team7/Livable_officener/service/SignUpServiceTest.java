package fastcampus.team7.Livable_officener.service;


import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.global.exception.DuplicatedPhoneNumberException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundBuildingException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundCompanyException;
import fastcampus.team7.Livable_officener.global.exception.NotVerifiedPhoneNumberException;
import fastcampus.team7.Livable_officener.repository.BuildingRepository;
import fastcampus.team7.Livable_officener.repository.CompanyRepository;
import fastcampus.team7.Livable_officener.repository.PhoneAuthDTORedisRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignUpServiceTest {

    @InjectMocks
    SignUpService signUpService;

    @Mock
    CompanyRepository companyRepository;
    @Mock
    BuildingRepository buildingRepository;
    @Mock
    PhoneAuthDTORedisRepository phoneAuthDTORedisRepository;
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

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
        List<BuildingWithCompaniesDTO> result = signUpService.getBuildingWithCompanies(keyword);

        // then
        BuildingWithCompaniesDTO expectedDto = result.get(0);
        List<CompanyDTO> companyDTOS = expectedDto.getCompanies();

        assertThat(expectedDto.getBuildingName()).isEqualTo(building.getName());
        assertThat(expectedDto.getBuildingAddress()).isEqualTo(building.getRegion() + " " + building.getCity() + " " + building.getStreet() + " " + building.getZipcode());
        assertThat(companyDTOS.get(0).getName()).isEqualTo("진회사");
        assertThat(companyDTOS.get(1).getName()).isEqualTo("칠리버블");
        assertThat(companyDTOS.get(2).getName()).isEqualTo("식스센스");

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
        List<BuildingWithCompaniesDTO> result = signUpService.getBuildingWithCompanies(keyword);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("휴대폰 인증번호 요청시 응답값의 인증코드와 저장된 인증코드가 같은지 테스트")
    public void givenPhoneNumberAndName_whenRequest_thenReturnVerifyCode() {

        // given
        final String name = "고길동";
        final String phoneNumber = "010-1234-5678";
        final String verifyCode = "982752";
        PhoneAuthDTO phoneAuthDTO = PhoneAuthDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(verifyCode)
                .build();

        given(phoneAuthDTORedisRepository.save(any(PhoneAuthDTO.class))).willReturn(phoneAuthDTO);

        // when
        PhoneAuthResponseDTO result = signUpService.getPhoneAuthCode(PhoneAuthRequestDTO.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .build());

        // then
        assertThat(result.getVerifyCode()).isEqualTo(verifyCode);

    }

    @Test
    @DisplayName("휴대폰 인증번호 요청시 이미 등록된 핸드폰 번호 일 경우 예외 발생")
    public void givenPhoneNumberAndName_whenRequest_thenExceptionDuplicatedPhoneNumber() {

        // given
        final String name = "고길동";
        final String phoneNumber = "01012345678";

        given(userRepository.findByPhoneNumber(any(String.class))).willThrow(new DuplicatedPhoneNumberException());

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
    @DisplayName("휴대폰 인증코드 확인 요청시 성공하는 경우 True 를 반환하는지 테스트")
    public void givenVerifyCode_whenConfirmRequest_thenConfirmSuccess() {

        // given
        final String phoneNumber = "01012345678";
        final String verifyCode = "135791";

        PhoneAuthDTO phoneAuthDTO = PhoneAuthDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(verifyCode)
                .build();

        given(phoneAuthDTORedisRepository.findById(phoneNumber)).willReturn(Optional.of(phoneAuthDTO));

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

        given(phoneAuthDTORedisRepository.findById(phoneNumber)).willThrow(new NotVerifiedPhoneNumberException());

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
    @DisplayName("휴대폰 인증코드 확인 요청시 검증에 실패하는 경우 False 를 반환 하는지 테스트")
    public void givenVerifyCode_whenConfirmRequest_thenNotVerifyCode() {

        // given
        final String phoneNumber = "01012345678";
        final String verifyCode = "135791";
        final String failureVerifyCode = "888888";

        PhoneAuthDTO phoneAuthDTO = PhoneAuthDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(verifyCode)
                .build();

        PhoneAuthConfirmDTO phoneAuthConfirmDTO = PhoneAuthConfirmDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(failureVerifyCode)
                .build();

        given(phoneAuthDTORedisRepository.findById(phoneNumber)).willReturn(Optional.of(phoneAuthDTO));

        // when
        boolean isConfirm = signUpService.confirmVerifyCode(phoneAuthConfirmDTO);

        // then
        assertThat(isConfirm).isFalse();
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
    @DisplayName("회원가입시 이미 등록되어 있는 회원일 경우 예외 발생")
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
        given(userRepository.existsByEmail(email)).willThrow(DuplicatedPhoneNumberException.class);

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
