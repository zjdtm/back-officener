package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.dto.ElevatorDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.ElevatorRepository;
import fastcampus.team7.Livable_officener.service.ElevatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ElevatorController {

    private final ElevatorRepository elevatorRepository;

    private final ElevatorService elevatorService;

    @GetMapping("/api/elevator")
    public ResponseEntity<APIDataResponse<List<ElevatorDTO>>> getAllElevators(){
        return elevatorService.getAllElevators();
    }

}
