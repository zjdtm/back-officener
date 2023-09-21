package fastcampus.team7.Livable_officener.dto.chat;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.TextMessage;

@RequiredArgsConstructor
@Getter
public class SendChatDTO {
    private final Room room;
    private final User sender;
    private final TextMessage message;
}
