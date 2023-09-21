package fastcampus.team7.Livable_officener.global.websocket;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<Long, Collection<WebSocketSession>> roomIdToSessions = new ConcurrentHashMap<>();

    public void addSessionToRoom(Long roomId, WebSocketSession session) {
        Collection<WebSocketSession> sessions;
        try {
            sessions = getWebSocketSessions(roomId);
        } catch (NotFoundRoomException e) {
            sessions = Collections.synchronizedList(new ArrayList<>());
            roomIdToSessions.put(roomId, sessions);
            sessions.add(session);
            return;
        }

        Long requestUserId = getSessionUserId(session);
        Optional<WebSocketSession> duplicateUserSession = sessions.stream()
                .filter(sess -> getSessionUserId(sess).equals(requestUserId))
                .findFirst();
        if (duplicateUserSession.isPresent()) {
            throw new IllegalStateException("웹소켓 세션은 채팅방마다 참여자별로 하나만 연결 가능합니다.");
        }
        sessions.add(session);
    }

    private static Long getSessionUserId(WebSocketSession session) {
        User user = (User) session.getAttributes().get("user");
        return user.getId();
    }

    public void removeSessionFromRoom(Long roomId, WebSocketSession session) {
        Collection<WebSocketSession> sessions = getWebSocketSessions(roomId);
        sessions.remove(session);
    }

    public void send(Long roomId, TextMessage message) throws IOException {
        Collection<WebSocketSession> webSocketSessions = getWebSocketSessions(roomId);
        for (WebSocketSession webSocketSession : webSocketSessions) {
            webSocketSession.sendMessage(message);
        }
    }

    private Collection<WebSocketSession> getWebSocketSessions(Long roomId) {
        var ret = roomIdToSessions.get(roomId);
        if (ret == null) {
            throw new NotFoundRoomException();
        }
        return ret;
    }
}
