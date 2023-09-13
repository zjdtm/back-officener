package fastcampus.team7.Livable_officener.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.Message;
import org.springframework.security.web.server.authentication.SwitchUserWebFilter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@RequiredArgsConstructor
@Getter
public class TestRoom {
    private static final Comparator<WebSocketSession> WS_COMP = Comparator.comparing(WebSocketSession::getId);

    private Long id;

    private String name;

    private final Set<WebSocketSession> sessions;

    public static TestRoom create(String name) {
        TestRoom testRoom = new TestRoom(new ConcurrentSkipListSet<>(WS_COMP));
        testRoom.id = RoomIdGenerator.createId();
        testRoom.name = name;
        return testRoom;
    }

    private static class RoomIdGenerator {
        private static Long id = 0L;

        public static Long createId() {
            id += 1;
            return id;
        }
    }

    public void addSession(WebSocketSession webSocketSession) {
        sessions.add(webSocketSession);
    }

    public void sendMessage(TextMessage textMessage) throws IOException {
        for (WebSocketSession connectedSession : sessions) {
            connectedSession.sendMessage(textMessage);
        }
    }

    public void removeSession(WebSocketSession webSocketSession) {
        sessions.remove(webSocketSession);
    }
}
