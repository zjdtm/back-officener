package fastcampus.team7.Livable_officener.dto.chat;

import fastcampus.team7.Livable_officener.domain.Chat;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
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
    private final String content;
    private final Long senderId;
    private final LocalDateTime sendTime;

    public static GetMessageDTO from(Chat chat) {
        final ChatType messageType = chat.getType();
        final User sender = chat.getSender();
        String content = chat.getContent();
        if (content == null) {
            content = messageType.getSystemMessageContent(sender);
        }
        return GetMessageDTO.builder()
                .messageId(chat.getId())
                .messageType(messageType.name())
                .content(content)
                .senderId(sender.getId())
                .sendTime(chat.getCreatedAt())
                .build();
    }
}
