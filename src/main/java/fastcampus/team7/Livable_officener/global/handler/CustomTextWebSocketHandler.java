package fastcampus.team7.Livable_officener.global.handler;

import fastcampus.team7.Livable_officener.domain.TestRoom;
import fastcampus.team7.Livable_officener.repository.TestRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
public class CustomTextWebSocketHandler extends TextWebSocketHandler {

    private final TestRoomRepository testRoomRepository;

    public CustomTextWebSocketHandler(TestRoomRepository testRoomRepository) {
        this.testRoomRepository = testRoomRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long roomId = getRoomId(session);

        TestRoom testRoom = getTestRoom(roomId);
        testRoom.addSession(session);
        log.info("클라이언트 연결 roomId: {}, sessionId: {}", roomId, session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        log.info(message.getPayload());

        Long roomId = getRoomId(session);
        getTestRoom(roomId).sendMessage(message);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        Long roomId = getRoomId(session);

        TestRoom testRoom = getTestRoom(roomId);
        testRoom.removeSession(session);

        log.info("클라이언트 연결 해제 roomId:: {}, sessionId: {}", roomId, session.getId());
    }

    private static Long getRoomId(WebSocketSession session) {
        return (Long) session.getAttributes().get("roomId");
    }

    private TestRoom getTestRoom(Long roomId) {
        return testRoomRepository.findById(roomId).get();
    }
}
