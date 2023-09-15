package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.dto.BuildingWithCompaniesDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.service.SignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SignUpController {

    @Autowired
    private SignUpService signUpService;

    @GetMapping("/building")
    public ResponseEntity<APIDataResponse<List<BuildingWithCompaniesDTO>>> getAllBuildings(@RequestParam("name") String keyword) {

        List<BuildingWithCompaniesDTO> result = signUpService.getBuildingWithCompanies(keyword);

        return APIDataResponse.of(result);

    }

}
