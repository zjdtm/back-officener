package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.dto.ElevatorDTO;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.ElevatorRepository;
import fastcampus.team7.Livable_officener.service.ElevatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ElevatorController {

    private final ElevatorRepository elevatorRepository;

    private final ElevatorService elevatorService;

    private final JwtProvider jwtProvider;

    @GetMapping("/api/elevator")
    public ResponseEntity<APIDataResponse<List<ElevatorDTO>>> getAllElevators(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){

        return elevatorService.getAllElevators();
    }

}
