package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Elevator;
import fastcampus.team7.Livable_officener.domain.UserElevator;
import fastcampus.team7.Livable_officener.dto.ElevatorDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.global.util.GenerateSig;
import fastcampus.team7.Livable_officener.repository.ElevatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ElevatorService {

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.secret}")
    private String apiSecret;

    private WebClient client;

    private final ElevatorRepository elevatorRepository;

    @PostConstruct
    public void webClientInit() {
        client = WebClient.builder()
                .baseUrl("http://13.125.50.47:8080")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader("ts", GenerateSig.generateTimestamp())
                .defaultHeader("nonce", GenerateSig.generateNonce())
                .defaultHeader("signature", GenerateSig.generateSignature(apiKey, apiSecret))
                .build();
    }

    public ResponseEntity<APIDataResponse<List<ElevatorDTO>>> getAllElevators() {
        // 모든 엘리베이터 정보를 조회
        List<Elevator> elevators = elevatorRepository.findAll();

        // Elevator 엔티티를 ElevatorDTO로 변환
        List<ElevatorDTO> elevatorDTOs = elevators.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 공통 응답 클래스에 데이터를 담아서 반환
        ResponseEntity<APIDataResponse<List<ElevatorDTO>>> responseEntity = APIDataResponse.of(
                HttpStatus.OK, "성공", elevatorDTOs);

        return responseEntity;
    }

    public void setElevator(List<Long> selectedIds){
        List<UserElevator> userElevators = new ArrayList<>();
        for (Long id : selectedIds) {
            UserElevator userElevator = new UserElevator();
            userElevator.setElevatorId(id);
            userElevators.add(userElevator);
        }

    }

    private ElevatorDTO convertToDTO(Elevator elevator) {
        ElevatorDTO elevatorDTO = new ElevatorDTO();
        elevatorDTO.setId(elevator.getId());
        elevatorDTO.setFloor(elevator.getFloor());
        elevatorDTO.setDirection(elevator.getDirection());
        elevatorDTO.setStatus(elevator.getStatus());
        return elevatorDTO;
    }


}
