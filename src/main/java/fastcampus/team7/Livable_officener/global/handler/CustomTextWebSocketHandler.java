package fastcampus.team7.Livable_officener.global.handler;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.SendChatDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
@RequiredArgsConstructor
public class CustomTextWebSocketHandler extends TextWebSocketHandler {

    private static final Comparator<WebSocketSession> WS_COMP = Comparator.comparing(WebSocketSession::getId);

    private final Map<Long, Set<WebSocketSession>> roomIdToSessionSet = new ConcurrentHashMap<>();
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Room room = getRoom(session);
        Set<WebSocketSession> sessionSet = roomIdToSessionSet.get(room.getId());
        if (sessionSet == null) {
            sessionSet = new ConcurrentSkipListSet<>(WS_COMP);
            roomIdToSessionSet.put(room.getId(), sessionSet);
        }
        sessionSet.add(session);

        User sender = getSender(session);
        log.info("클라이언트 연결 roomId: {}, senderId: {}, sessionId: {}", room.getId(), sender.getId(), session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Room room = getRoom(session);
        User sender = getSender(session);
        Set<WebSocketSession> sessionSet = roomIdToSessionSet.get(room.getId());
        log.info("roomId: {}, senderId: {}, content: {}", room.getId(), sender.getId(), message.getPayload());

        SendChatDTO sendChatDTO = new SendChatDTO(room, sender, message, ChatType.TALK, sessionSet);
        chatService.send(sendChatDTO);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Room room = getRoom(session);
        Set<WebSocketSession> sessionSet = roomIdToSessionSet.get(room.getId());
        sessionSet.remove(session);

        User sender = getSender(session);
        log.info("클라이언트 연결 해제 roomId: {}, senderId: {}, sessionId: {}", room.getId(), sender.getId(), session.getId());
    }

    private Room getRoom(WebSocketSession session) {
        return (Room) session.getAttributes().get("room");
    }

    private User getSender(WebSocketSession session) {
        return (User) session.getAttributes().get("sender");
    }
}
