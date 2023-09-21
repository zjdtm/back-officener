package fastcampus.team7.Livable_officener.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class SendPayloadDTO {

    private final ChatType messageType;
    private final String content;
    private final LocalDateTime sendTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long senderId;

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
}
