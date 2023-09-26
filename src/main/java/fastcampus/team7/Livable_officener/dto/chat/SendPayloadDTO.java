package fastcampus.team7.Livable_officener.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendPayloadDTO {

    private ChatType messageType;
    private String content;
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sendTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long senderId;

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
}
