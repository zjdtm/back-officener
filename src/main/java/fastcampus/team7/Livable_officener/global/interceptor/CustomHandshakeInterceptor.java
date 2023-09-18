package fastcampus.team7.Livable_officener.global.interceptor;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

@RequiredArgsConstructor
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        Room room = getRoom(request);
        User sender = getSender(request);

        attributes.put("room", room);
        attributes.put("sender", sender);

        return true;
    }

    private Room getRoom(ServerHttpRequest request) {
        Long roomId = getRoomId(request);
        return chatRoomRepository.findById(roomId)
                // TODO NotFoundRoomException 정의
                .orElseThrow(IllegalArgumentException::new);
    }

    private User getSender(ServerHttpRequest request) {
        return (User) request.getPrincipal();
    }

    private static Long getRoomId(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String roomIdStr = path.substring(path.lastIndexOf('/') + 1);
        return Long.parseLong(roomIdStr);
    }
}
