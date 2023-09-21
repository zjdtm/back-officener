package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Elevator;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.domain.UserElevator;
import fastcampus.team7.Livable_officener.dto.ElevatorDTO;
import fastcampus.team7.Livable_officener.global.constant.ElevatorStatus;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.global.util.GenerateSig;
import fastcampus.team7.Livable_officener.repository.ElevatorRepository;
import fastcampus.team7.Livable_officener.repository.UserElevatorRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
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

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private final UserElevatorRepository userElevatorRepository;
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

    public ResponseEntity<APIDataResponse<List<ElevatorDTO>>> getAllElevators(String token) {
        User user = userRepository.findByEmail(jwtProvider.getEmail(token))
                .orElse(null);
        List<UserElevator> userElevators = userElevatorRepository.findByUserId(user.getId()).orElse(null);
        List<ElevatorDTO> elevatorDTOs = new ArrayList<>();

            if(userElevators.isEmpty()) {
                List<Elevator> elevators = elevatorRepository.findAll();
                for (Elevator elevator : elevators) {
                    elevatorDTOs.add(convertToDTO(elevator));
                }
            }
            else {
                for (UserElevator userElevator : userElevators) {
                    Elevator elevator = elevatorRepository.findById(userElevator.getElevatorId())
                            .orElseThrow(() -> new RuntimeException("해당하는 엘리베이터가 없습니다"));
                    elevatorDTOs.add(convertToDTO(elevator));
                }
            }
            ResponseEntity<APIDataResponse<List<ElevatorDTO>>> responseEntity = APIDataResponse.of(
                    HttpStatus.OK, "성공", elevatorDTOs);
            return responseEntity;
        }

    public ResponseEntity<APIDataResponse<String>> setElevator(List<Long> selectedIds,String token){
        User user = userRepository.findByEmail(jwtProvider.getEmail(token))
                .orElseThrow(() -> new RuntimeException("토큰에 일치하는 유저가 없습니다"));
        List<UserElevator> userElevators = new ArrayList<>();
        for (Long id : selectedIds) {
            UserElevator userElevator = new UserElevator();
            userElevator.setUserId(user.getId());
            userElevator.setElevatorId(id);
            userElevators.add(userElevator);
        }
        ResponseEntity<APIDataResponse<String>> responseEntity = APIDataResponse.empty(
                HttpStatus.OK, "엘리베이터 설정에 성공했습니다.");
        return responseEntity;
    }

    private ElevatorDTO convertToDTO(Elevator elevator) {
        ElevatorDTO elevatorDTO = new ElevatorDTO();
        if (elevator.getStatus().equals(ElevatorStatus.REPAIR)) {
            elevatorDTO.setId(elevator.getId());
            elevatorDTO.setStatus(elevator.getStatus());
            return elevatorDTO;
        } else {
            elevatorDTO.setId(elevator.getId());
            elevatorDTO.setFloor(elevator.getFloor());
            elevatorDTO.setDirection(elevator.getDirection());
            elevatorDTO.setStatus(elevator.getStatus());
            return elevatorDTO;
        }
    }
}
