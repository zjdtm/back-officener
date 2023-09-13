package fastcampus.team7.Livable_officener.global.config;

import fastcampus.team7.Livable_officener.global.handler.CustomTextWebSocketHandler;
import fastcampus.team7.Livable_officener.global.interceptor.CustomHandshakeInterceptor;
import fastcampus.team7.Livable_officener.repository.TestRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TestRoomRepository testRoomRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customTextWebSocketHandler(), "/api/chat/*")
                .setAllowedOrigins("*")
                .addInterceptors(customHandshakeInterceptor())
//                .setAllowedOrigins("FRONT_ORIGIN") // 추후 프론트엔드 URL 결정되면 주석 풀기

        ;
    }

    @Bean
    public WebSocketHandler customTextWebSocketHandler() {
        return new CustomTextWebSocketHandler(testRoomRepository);
    }

    @Bean
    public HandshakeInterceptor customHandshakeInterceptor() {
        return new CustomHandshakeInterceptor(testRoomRepository);
    }
}
