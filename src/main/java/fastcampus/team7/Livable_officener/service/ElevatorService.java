package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.dto.ElevatorDTO;
import fastcampus.team7.Livable_officener.global.util.GenerateSig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;


@Service
@RequiredArgsConstructor
public class ElevatorService {

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.secret}")
    private String apiSecret;

    private WebClient client;

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

//    public Flux<ElevatorDTO> getElevator(){
//        client.get()
//                .uri("/api/v1/el/lineid/{lineid}/s)
//    }

}
