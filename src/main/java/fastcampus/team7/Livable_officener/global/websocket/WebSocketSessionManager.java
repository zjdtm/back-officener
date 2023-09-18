package fastcampus.team7.Livable_officener.global.websocket;

import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<Long, Collection<WebSocketSession>> roomIdToSessions = new ConcurrentHashMap<>();

    public void addSessionToRoom(Long roomId, WebSocketSession session) {
        Collection<WebSocketSession> sessions = getWebSocketSessions(roomId);
        if (sessions == null) {
            sessions = Collections.synchronizedList(new ArrayList<>());
            roomIdToSessions.put(roomId, sessions);
        }
        sessions.add(session);
    }

    public Collection<WebSocketSession> getWebSocketSessions(Long roomId) {
        var ret = roomIdToSessions.get(roomId);
        if (ret == null) {
            throw new NotFoundRoomException();
        }
        return ret;
    }

    public void removeSessionFromRoom(Long roomId, WebSocketSession session) {
        Collection<WebSocketSession> sessions = getWebSocketSessions(roomId);
        sessions.remove(session);
    }
}
