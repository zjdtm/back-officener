package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SignUpController {

    @Autowired
    private SignUpService signUpService;

    @GetMapping("/building")
    public ResponseEntity<APIDataResponse<Map<String, List<BuildingWithCompaniesDTO>>>> getAllBuildings(@RequestParam("name") String keyword) {

        Map<String, List<BuildingWithCompaniesDTO>> buildingWithCompanies = signUpService.getBuildingWithCompanies(keyword);

        return APIDataResponse.of(HttpStatus.OK, buildingWithCompanies);

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
    public ResponseEntity<APIDataResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request) {

        LoginResponseDTO result = signUpService.login(request);

        return APIDataResponse.of(HttpStatus.OK, result);

    }

}
