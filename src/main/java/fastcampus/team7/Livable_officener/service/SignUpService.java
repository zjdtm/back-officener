package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.global.exception.*;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.global.util.RedisUtil;
import fastcampus.team7.Livable_officener.repository.BuildingRepository;
import fastcampus.team7.Livable_officener.repository.CompanyRepository;
import fastcampus.team7.Livable_officener.repository.PhoneAuthDTORedisRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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

    private final JwtProvider jwtProvider;

    private final RedisUtil redisUtil;

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

        if (userRepository.existsByPhoneNumber(requestPhoneNumber)) {
            throw new DuplicatedPhoneNumberException();
        }

        String phoneAuthCode = redisUtil.getPhoneAuthCode(requestPhoneNumber);

        if (ObjectUtils.isEmpty(phoneAuthCode)) {
            redisUtil.setPhoneAuthCode(requestPhoneNumber);

            return PhoneAuthResponseDTO.builder()
                    .verifyCode(redisUtil.getPhoneAuthCode(requestPhoneNumber))
                    .build();
        }

        return PhoneAuthResponseDTO.builder()
                .verifyCode(redisUtil.changePhoneAuthCode(requestPhoneNumber))
                .build();

    }

    @Transactional(readOnly = true)
    public boolean confirmVerifyCode(PhoneAuthConfirmDTO request) {

        String requestPhoneNumber = request.getPhoneNumber();
        String requestVerifyCode = request.getVerifyCode();

        if (!redisUtil.hasPhoneAuthCode(requestPhoneNumber)) {
            throw new NotVerifiedPhoneNumberException();
        }
//        PhoneAuthDTO findPhoneAuthDTO = phoneAuthDTORedisRepository.findById(requestPhoneNumber)
//                .orElseThrow(() -> new NotVerifiedPhoneNumberException());

//        if (findPhoneAuthDTO.getVerifyCode().equals(requestVerifyCode)) {
//            return true;
//        }
//
//        throw new NotVerifiedPhoneAuthCodeException();

        if (!redisUtil.getPhoneAuthCode(requestPhoneNumber).equals(requestVerifyCode)) {
            throw new NotVerifiedPhoneAuthCodeException();
        }

        return true;

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

    public Map<String, LoginResponseDTO> login(LoginRequestDTO request) {

        String requestEmail = request.getEmail();
        String requestPassword = request.getPassword();

        User user = userRepository.findByEmail(requestEmail)
                .orElseThrow(() -> new NotFoundUserException());

        if (!passwordEncoder.matches(requestPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }

        BuildingDTO buildingDTO = BuildingDTO.builder()
                .id(user.getBuilding().getId())
                .buildingName(user.getBuilding().getName())
                .buildingAddress(user.getBuilding().getAddress())
                .build();

        CompanyDTO companyDTO = CompanyDTO.builder()
                .id(user.getCompany().getId())
                .officeName(user.getCompany().getName())
                .officeNum(user.getCompany().getAddress())
                .build();

        String token = jwtProvider.createToken(requestEmail);

        LoginResponseDTO responseBody = LoginResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .building(buildingDTO)
                .company(companyDTO)
                .token(token)
                .build();

        Map<String, LoginResponseDTO> loginResponseMap = new HashMap<>();
        loginResponseMap.put("userInfo", responseBody);

        return loginResponseMap;
    }

    public void logout(User user, String authorization) {

        String bearerTokenPrefix = jwtProvider.getBearerTokenPrefix(authorization);
        Long expirationTime = jwtProvider.getExpirationTime(bearerTokenPrefix);

        redisUtil.setBlackList(bearerTokenPrefix, user.getEmail(), expirationTime);

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
