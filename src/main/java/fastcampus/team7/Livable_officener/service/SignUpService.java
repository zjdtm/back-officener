package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.repository.BuildingRepository;
import fastcampus.team7.Livable_officener.repository.CompanyRepository;
import fastcampus.team7.Livable_officener.repository.PhoneAuthDTORedisRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SignUpService {

    private final BuildingRepository buildingRepository;
    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;
    private final PhoneAuthDTORedisRepository phoneAuthDTORedisRepository;

    @Transactional(readOnly = true)
    public List<BuildingWithCompaniesDTO> getBuildingWithCompanies(String keyword) {

        List<Building> buildings = buildingRepository.findBuildingsByNameContaining(keyword);

        List<BuildingWithCompaniesDTO> buildingDTOs = new ArrayList<>();

        for (Building building : buildings) {
            String address = getAddress(building);

            BuildingWithCompaniesDTO buildingDTO = BuildingWithCompaniesDTO.builder()
                    .id(building.getId())
                    .buildingName(building.getName())
                    .buildingAddress(address)
                    .companies(getCompanies(companyRepository.findCompaniesByBuildingName(building.getName())))
                    .build();

            buildingDTOs.add(buildingDTO);
        }

        return buildingDTOs;
    }

    public String getPhoneAuthCode(PhoneAuthRequestDTO request) {

        String requestPhoneNumber = request.getPhoneNumber();

        userRepository.findByPhoneNumber(requestPhoneNumber)
                .ifPresent(value -> new RuntimeException("이미 등록된 휴대폰 번호입니다."));

        PhoneAuthDTO findPhoneAuthDTO = phoneAuthDTORedisRepository.findById(requestPhoneNumber)
                .orElse(null);

        String newVerifyCode = generateVerifyCode();
        if (Objects.isNull(findPhoneAuthDTO)) {
            phoneAuthDTORedisRepository.save(PhoneAuthDTO.builder()
                    .phoneNumber(requestPhoneNumber)
                    .verifyCode(newVerifyCode)
                    .build());
        } else {
            findPhoneAuthDTO.changeVerifyCode(newVerifyCode);
        }

        return newVerifyCode;

    }

    @Transactional(readOnly = true)
    public boolean confirmVerifyCode(PhoneAuthConfirmDTO request) {

        String requestPhoneNumber = request.getPhoneNumber();
        String requestVerifyCode = request.getVerifyCode();

        PhoneAuthDTO findPhoneAuthDTO = phoneAuthDTORedisRepository.findById(requestPhoneNumber)
                .orElseThrow(() -> new RuntimeException("인증되지 않은 핸드폰 번호입니다."));

        return findPhoneAuthDTO.getVerifyCode().equals(requestVerifyCode);

    }

    public void signUp(SignUpRequestDTO request) {

        String requestBuildingName = request.getBuildingName();
        Building building = buildingRepository.findByName(requestBuildingName)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 건물명입니다."));

        User user = request.toEntity(building);

        userRepository.save(user);

    }

    private String generateVerifyCode() {
        Random random = new Random();
        String newVerifyCode = "";
        for (int i = 0; i < 6; i++) {
            newVerifyCode += Integer.toString(random.nextInt(10));
        }
        return newVerifyCode;
    }

    private String getAddress(Building building) {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(building.getRegion());
        sj.add(building.getCity());
        sj.add(building.getStreet());
        sj.add(building.getZipcode());
        return sj.toString();
    }

    private List<CompanyDTO> getCompanies(List<Company> companies) {
        List<CompanyDTO> companyDTOs = new ArrayList();
        for (Company company : companies) {
            CompanyDTO companyDTO = new CompanyDTO(
                    company.getId(),
                    company.getName(),
                    company.getAddress()
            );
            companyDTOs.add(companyDTO);
        }
        return companyDTOs;
    }

}
