package fastcampus.team7.Livable_officener.global.websocket;

import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import org.springframework.web.socket.WebSocketSession;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class WebSocketSessionManager {
    private static final Comparator<WebSocketSession> WS_COMP = Comparator.comparing(WebSocketSession::getId);

    private final Map<Long, Set<WebSocketSession>> roomIdToSessionSet = new ConcurrentHashMap<>();

    public void addSessionToRoom(Long roomId, WebSocketSession session) {
        Set<WebSocketSession> wsSessionSet = getSessionSetOfRoom(roomId);
        if (wsSessionSet == null) {
            wsSessionSet = new ConcurrentSkipListSet<>(WS_COMP);
            roomIdToSessionSet.put(roomId, wsSessionSet);
        }
        wsSessionSet.add(session);
    }

    public Set<WebSocketSession> getSessionSetOfRoom(Long roomId) {
        var ret = roomIdToSessionSet.get(roomId);
        if (ret == null) {
            throw new NotFoundRoomException();
        }
        return ret;
    }

    public void removeSessionFromRoom(Long roomId, WebSocketSession session) {
        Set<WebSocketSession> wsSessionSet = getSessionSetOfRoom(roomId);
        wsSessionSet.remove(session);
    }
}
