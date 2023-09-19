package fastcampus.team7.Livable_officener.service;


import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.dto.BuildingWithCompaniesDTO;
import fastcampus.team7.Livable_officener.dto.CompanyDTO;
import fastcampus.team7.Livable_officener.dto.PhoneAuthDTO;
import fastcampus.team7.Livable_officener.dto.PhoneAuthRequestDTO;
import fastcampus.team7.Livable_officener.repository.BuildingRepository;
import fastcampus.team7.Livable_officener.repository.CompanyRepository;
import fastcampus.team7.Livable_officener.repository.PhoneAuthDTORedisRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    @DisplayName("[GET] /api/building?name={buildingName} - Service 성공 테스트")
    public void searchBuildingWithCompanies_Success() {

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
    @DisplayName("[GET] /api/building?name={buildingName} - Service 실패 테스트")
    public void searchBuildingWithCompanies_Failure() {

        // given
        final String keyword = "존재하지 않는 빌딩";

        given(buildingRepository.findBuildingsByNameContaining(keyword)).willReturn(Collections.emptyList());

        // when
        List<BuildingWithCompaniesDTO> result = signUpService.getBuildingWithCompanies(keyword);

        // then
        assertThat(result).isEmpty();
    }

//    // TODO : 인증번호 요청 성공 테스트 코드 작성
//    @Test
//    @DisplayName("휴대폰 인증번호 요청시 적절한 값 출력 테스트")
//    public void whenPhoneNumberSend_thenSuccessPhoneAuthCode() {
//
//        // given
//        final String phoneNumber = "010-1234-5678";
//        final String verifyCode = "982752";
//        PhoneAuthDTO phoneAuthDTO = PhoneAuthDTO.builder()
//                .phoneNumber(phoneNumber)
//                .verifyCode(verifyCode)
//                .build();
//
//        given(userRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
//        given(phoneAuthDTORedisRepository.save(any(PhoneAuthDTO.class))).willReturn(phoneAuthDTO);
//        given(phoneAuthDTORedisRepository.findById(phoneNumber)).willReturn(Optional.empty());
//
//        // when
//        String result = signUpService.getPhoneAuthCode(PhoneAuthRequestDTO.builder()
//                .name("고길동")
//                .phoneNumber(phoneNumber)
//                .build());
//
//        Optional<PhoneAuthDTO> findById = phoneAuthDTORedisRepository.findById(phoneNumber);
//
//        // then
//        verify(phoneAuthDTORedisRepository).save(phoneAuthDTO);
//        assertThat(result).isEqualTo(findById.get().getVerifyCode());
//
//    }

    // TODO : 인증번호 업데이트 성공 테스트 코드 작성
//    @Test
//    @DisplayName("휴대폰 인증번호 재요청시 업데이트 된 새로운 인증번호 저장 및 출력 테스트")
//    public void whenPhoneNumberSend_thenSuccessUpdatedPhoneAuthCode() {
//
//        // given
//        final String phoneNumber = "010-1234-5678";
//        final String verifyCode = "982752";
//        PhoneAuthDTO phoneAuthDTO = PhoneAuthDTO.builder()
//                .phoneNumber(phoneNumber)
//                .verifyCode(verifyCode)
//                .build();
//
//        given(userRepository.findByPhoneNumber(phoneNumber)).willReturn(Optional.empty());
//        given(phoneAuthDTORedisRepository.findById(phoneNumber)).willReturn(Optional.of(phoneAuthDTO));
//
//        // when
//        String result = signUpService.getPhoneAuthCode(PhoneAuthRequestDTO.builder()
//                .name("고길동")
//                .phoneNumber(phoneNumber)
//                .build());
//
//        // then
////        assertThat(result).isEqualTo(findById.get().getVerifyCode());
//
//    }

//    @Test
//    @DisplayName("휴대폰 인증번호 요청시 이미 등록된 핸드폰 번호 일 경우 에러")
//    public void whenPhoneNumberSend_thenFailedDuplicatedPhoneNumber() {
//
//        // given
//        given(userRepository.findByPhoneNumber(any(String.class))).willThrow(new RuntimeException("이미 등록된 휴대폰 번호입니다."));
//
//        // when & then
//        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
//            signUpService.getPhoneAuthCode(PhoneAuthRequestDTO.builder()
//                    .name("고길동")
//                    .phoneNumber("01012345678")
//                    .build());
//        });
//
//        assertEquals("이미 등록된 휴대폰 번호입니다.", exception.getMessage());
//
//    }

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
