package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.BuildingWithCompaniesDTO;
import fastcampus.team7.Livable_officener.dto.LoginDTO;
import fastcampus.team7.Livable_officener.dto.LoginDTO.LoginRequestDTO;
import fastcampus.team7.Livable_officener.dto.PhoneAuthDTO.PhoneAuthConfirmDTO;
import fastcampus.team7.Livable_officener.dto.SignUpRequestDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static fastcampus.team7.Livable_officener.dto.PhoneAuthDTO.PhoneAuthRequestDTO;
import static fastcampus.team7.Livable_officener.dto.PhoneAuthDTO.PhoneAuthResponseDTO;

@RestController
@RequestMapping("/api")
public class SignUpController {

    @Autowired
    private SignUpService signUpService;

    @GetMapping("/building")
    public ResponseEntity<APIDataResponse<BuildingWithCompaniesDTO>> getAllBuildings(@RequestParam("name") String keyword) {

        BuildingWithCompaniesDTO result = signUpService.getBuildingWithCompanies(keyword);

        return APIDataResponse.of(HttpStatus.OK, result);

    }

    @PostMapping("/auth")
    public ResponseEntity<APIDataResponse<PhoneAuthResponseDTO>> getPhoneAuthCode(@RequestBody PhoneAuthRequestDTO request) {

        PhoneAuthResponseDTO result = signUpService.getPhoneAuthCode(request);

        return APIDataResponse.of(HttpStatus.OK, result);

    }

    @PostMapping("/confirm")
    public ResponseEntity<APIDataResponse<String>> confirmPhoneAuthCode(@RequestBody PhoneAuthConfirmDTO request) {

        signUpService.confirmVerifyCode(request);

        return APIDataResponse.of(HttpStatus.OK, "인증이 완료되었습니다.");

    }


    @PostMapping("/signup")
    public ResponseEntity<APIDataResponse<String>> signUp(@RequestBody @Valid SignUpRequestDTO request) {

        signUpService.signUp(request);

        return APIDataResponse.of(HttpStatus.OK, "회원가입에 성공했습니다.");

    }

    @PostMapping("/login")
    public ResponseEntity<APIDataResponse<LoginDTO>> login(@RequestBody @Valid LoginRequestDTO request) {

        LoginDTO result = signUpService.login(request);

        return APIDataResponse.of(HttpStatus.OK, result);

    }

    @PostMapping("/logout")
    public ResponseEntity<APIDataResponse<String>> logout(
            @AuthenticationPrincipal User user,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        signUpService.logout(user, authorization);

        return APIDataResponse.of(HttpStatus.OK, "로그아웃에 성공하였습니다.");
    }

}
