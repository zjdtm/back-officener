package fastcampus.team7.Livable_officener.global.handler;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.SendChatDTO;
import fastcampus.team7.Livable_officener.global.websocket.WebSocketSessionManager;
import fastcampus.team7.Livable_officener.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomTextWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Room room = getRoom(session);
        webSocketSessionManager.addSessionToRoom(room.getId(), session);

        User sender = getSender(session);
        log.info("클라이언트 연결 roomId: {}, senderId: {}, sessionId: {}", room.getId(), sender.getId(), session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Room room = getRoom(session);
        User sender = getSender(session);
        log.info("roomId: {}, senderId: {}, payload: {}", room.getId(), sender.getId(), message.getPayload());

        chatService.send(new SendChatDTO(room, sender, message));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Room room = getRoom(session);
        webSocketSessionManager.removeSessionFromRoom(room.getId(), session);

        User sender = getSender(session);
        log.info("클라이언트 연결 해제 roomId: {}, senderId: {}, sessionId: {}",
                room.getId(), sender.getId(), session.getId());
    }

    private Room getRoom(WebSocketSession session) {
        return (Room) session.getAttributes().get("room");
    }

    private User getSender(WebSocketSession session) {
        return (User) session.getAttributes().get("sender");
    }
}
