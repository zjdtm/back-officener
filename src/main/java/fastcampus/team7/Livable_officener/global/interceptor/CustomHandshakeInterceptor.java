package fastcampus.team7.Livable_officener.global.interceptor;

import fastcampus.team7.Livable_officener.repository.TestRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final TestRoomRepository testRoomRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        String path = request.getURI().getPath();
        String roomIdStr = path.substring(path.lastIndexOf('/') + 1);
        Long roomId = Long.parseLong(roomIdStr);

        if (testRoomRepository.findById(roomId).isEmpty()) {
            return false;
        }

        attributes.put("roomId", roomId);

        return true;
    }
}
