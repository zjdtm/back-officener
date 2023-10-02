package fastcampus.team7.Livable_officener.global.websocket;

import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final JwtProvider jwtProvider;

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        final String accessToken = parseAccessToken(request);
        return jwtProvider.resolveUser(accessToken);
    }

    private static String parseAccessToken(ServerHttpRequest request) {
        final String query = request.getURI().getQuery();
        if (query == null) {
            throw new IllegalArgumentException("웹소켓에 인증 정보가 없습니다.");
        }

        final String strToFind = "ticket=";
        final int i = query.indexOf(strToFind);
        if (i == -1) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않은 웹소켓입니다.");
        }

        return query.substring(i + strToFind.length());
    }

}
