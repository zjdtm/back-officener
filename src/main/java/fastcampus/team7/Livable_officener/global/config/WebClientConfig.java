//package fastcampus.team7.Livable_officener.global.config;
//
//import fastcampus.team7.Livable_officener.global.response.MyResponse;
//import fastcampus.team7.Livable_officener.global.util.GenerateSig;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebClientConfig {
//
//    @Value("${my.api.key}")
//    private String apiKey;
//
//    @Value("${my.api.secret}")
//    private String apiSecret;
//
//    @Bean
//    public WebClient webClientBuilder(GenerateSig generateSig){
//
//        String signature = generateSig.generateSignature();
//        String nonce = generateSig.generateNonce();
//        String ts = generateSig.generateTimestamp();
//
//        return WebClient.builder() // 수정된 부분
//                .baseUrl("http://13.125.50.47:8080")
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                .defaultHeader("ts", ts) // 수정된 부분
//                .defaultHeader("nonce", nonce)
//                .defaultHeader("signature", signature)
//                .retrieve()
//                .bodyToMono(MyResponse.class);
//    }
//
//}
