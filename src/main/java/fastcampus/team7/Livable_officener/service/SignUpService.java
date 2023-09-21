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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

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
                .ifPresent(e -> new DuplicatedPhoneNumberException());

        PhoneAuthDTO findPhoneAuthDTO = phoneAuthDTORedisRepository.findById(requestPhoneNumber)
                .orElse(null);

        if (findPhoneAuthDTO == null) {
            PhoneAuthDTO savedPhoneAuthDTO = phoneAuthDTORedisRepository.save(PhoneAuthDTO.builder()
                    .phoneNumber(requestPhoneNumber)
                    .verifyCode(generateVerifyCode())
                    .build());
            return savedPhoneAuthDTO.getVerifyCode();
        }

        findPhoneAuthDTO.changeVerifyCode(generateVerifyCode());

        return findPhoneAuthDTO.getVerifyCode();

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
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 회사를 찾을 수 없습니다."));

        boolean existEmail = userRepository.existsByEmail(request.getEmail());

        if (existEmail) {
            throw new DuplicatedUserEmailException();
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
