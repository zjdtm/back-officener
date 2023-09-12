package fastcampus.team7.Livable_officener.global.config;

import fastcampus.team7.Livable_officener.global.handler.CustomTextWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customTextWebSocketHandler(), "/api/chat")
//                .setAllowedOrigins("FRONT_ORIGIN") // 추후 프론트엔드 URL 결정되면 주석 풀기
        ;
    }

    @Bean
    public WebSocketHandler customTextWebSocketHandler() {
        return new CustomTextWebSocketHandler();
    }

}
