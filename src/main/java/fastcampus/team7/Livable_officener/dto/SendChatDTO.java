package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;

@RequiredArgsConstructor
@Getter
public class SendChatDTO {
    private final Room room;
    private final User sender;
    private final TextMessage message;
    private final ChatType type;
    private final Set<WebSocketSession> sessionSet;
}
