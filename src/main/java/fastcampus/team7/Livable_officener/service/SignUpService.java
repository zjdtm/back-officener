package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.global.exception.*;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.repository.BuildingRepository;
import fastcampus.team7.Livable_officener.repository.CompanyRepository;
import fastcampus.team7.Livable_officener.repository.PhoneAuthDTORedisRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Map<String, List<BuildingWithCompaniesDTO>> getBuildingWithCompanies(String keyword) {

        List<Building> buildings = buildingRepository.findBuildingsByNameContaining(keyword);
        Map<String, List<BuildingWithCompaniesDTO>> buildingWithCompaniesMap = new HashMap<>();

        List<BuildingWithCompaniesDTO> buildingWithCompaniesDTOS = new ArrayList<>();

        for (Building building : buildings) {

            BuildingWithCompaniesDTO buildingDTO = BuildingWithCompaniesDTO.builder()
                    .id(building.getId())
                    .buildingName(building.getName())
                    .buildingAddress(building.getAddress())
                    .offices(getCompanies(companyRepository.findCompaniesByBuildingName(building.getName())))
                    .build();

            buildingWithCompaniesDTOS.add(buildingDTO);
        }

        buildingWithCompaniesMap.put("buildings", buildingWithCompaniesDTOS);

        return buildingWithCompaniesMap;
    }

    public PhoneAuthResponseDTO getPhoneAuthCode(PhoneAuthRequestDTO request) {

        String requestPhoneNumber = request.getPhoneNumber();

        userRepository.findByPhoneNumber(requestPhoneNumber)
                .ifPresent(e -> new DuplicatedPhoneNumberException());

        PhoneAuthDTO findPhoneAuthDTO = phoneAuthDTORedisRepository.findById(requestPhoneNumber)
                .orElse(null);

        if (findPhoneAuthDTO == null) {

            PhoneAuthDTO savedPhoneAuthDTO = phoneAuthDTORedisRepository.save(PhoneAuthDTO.builder()
                    .phoneNumber(requestPhoneNumber)
                    .verifyCode(generateVerifyCode())
                    .build());

            return PhoneAuthResponseDTO.builder()
                    .verifyCode(savedPhoneAuthDTO.getVerifyCode())
                    .build();
        }

        findPhoneAuthDTO.changeVerifyCode(generateVerifyCode());

        return PhoneAuthResponseDTO.builder()
                .verifyCode(findPhoneAuthDTO.getVerifyCode())
                .build();

    }

    @Transactional(readOnly = true)
    public boolean confirmVerifyCode(PhoneAuthConfirmDTO request) {

        String requestPhoneNumber = request.getPhoneNumber();
        String requestVerifyCode = request.getVerifyCode();

        PhoneAuthDTO findPhoneAuthDTO = phoneAuthDTORedisRepository.findById(requestPhoneNumber)
                .orElseThrow(() -> new NotVerifiedPhoneNumberException());

        return findPhoneAuthDTO.getVerifyCode().equals(requestVerifyCode);

    }

    public void signUp(SignUpRequestDTO request) {

        Building building = buildingRepository.findByName(request.getBuildingName())
                .orElseThrow(() -> new NotFoundBuildingException());

        Company company = companyRepository.findByName(request.getCompanyName())
                .orElseThrow(() -> new NotFoundCompanyException());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicatedUserEmailException();
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicatedPhoneNumberException();
        }

        User user = request.toEntity(building, company, passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

    }

    public LoginResponseDTO login(LoginRequestDTO request) {

        String requestEmail = request.getEmail();

        userRepository.findByEmail(requestEmail)
                .orElseThrow(() -> new NotFoundUserException());

        String token = jwtProvider.createToken(requestEmail);

        return LoginResponseDTO.builder()
                .token(token)
                .build();
    }

    private String generateVerifyCode() {
        Random random = new Random();
        String newVerifyCode = "";
        for (int i = 0; i < 6; i++) {
            newVerifyCode += Integer.toString(random.nextInt(10));
        }
        return newVerifyCode;
    }

    private List<CompanyDTO> getCompanies(List<Company> companies) {
        List<CompanyDTO> companyDTOS = new ArrayList();
        for (Company company : companies) {
            CompanyDTO companyDTO = new CompanyDTO(
                    company.getId(),
                    company.getName(),
                    company.getAddress()
            );
            companyDTOS.add(companyDTO);
        }
        return companyDTOS;
    }
}
