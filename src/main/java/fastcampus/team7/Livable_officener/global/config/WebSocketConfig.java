package fastcampus.team7.Livable_officener.global.config;

import fastcampus.team7.Livable_officener.global.handler.CustomTextWebSocketHandler;
import fastcampus.team7.Livable_officener.global.interceptor.CustomHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CustomTextWebSocketHandler customTextWebSocketHandler;
    private final CustomHandshakeInterceptor customHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customTextWebSocketHandler, "/api/chat/*")
                .setAllowedOrigins("*")
                .addInterceptors(customHandshakeInterceptor)
//                .setAllowedOrigins("FRONT_ORIGIN") // 추후 프론트엔드 URL 결정되면 주석 풀기

        ;
    }

}
