package fastcampus.team7.Livable_officener.dto.chat;

import fastcampus.team7.Livable_officener.domain.Chat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class GetMessageDTO {

    private final Long messageId;
    private final String messageType;
    private final Long senderId;
    private final LocalDateTime sendTime;

    public static GetMessageDTO from(Chat chat) {
        return GetMessageDTO.builder()
                .messageId(chat.getId())
                .messageType(chat.getType().name())
                .senderId(chat.getSender().getId())
                .sendTime(chat.getCreatedAt())
                .build();
    }
}
