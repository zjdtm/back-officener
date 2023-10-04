package fastcampus.team7.Livable_officener.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "messageType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(
                value = SendPayloadDTO.Normal.class,
                names = {"TALK",
                        "CLOSE_PARTICIPATION",
                        "COMPLETE_REMITTANCE",
                        "COMPLETE_DELIVERY",
                        "COMPLETE_RECEIPT",
                        "REQUEST_EXIT",
                        "EXIT",
                        "KICK"}),
        @JsonSubTypes.Type(
                value = SendPayloadDTO.Enter.class,
                name = "ENTER")})
public class SendPayloadDTO {

    private ChatType messageType;
    private String content;
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sendTime;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long senderId;

    public SendPayloadDTO(ChatType messageType, Long senderId) {
        this(messageType, null, senderId);
    }

    public SendPayloadDTO(ChatType messageType, String content, Long senderId) {
        this (messageType, content, LocalDateTime.now(), senderId);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public static class Normal extends SendPayloadDTO {
    }

    @Getter
    @NoArgsConstructor
    public static class Enter extends SendPayloadDTO {
        private GetParticipantDTO newParticipant;

        public Enter(String content, RoomParticipant newParticipant) {
            super(ChatType.ENTER, content, newParticipant.getUser().getId());
            this.newParticipant = GetParticipantDTO.from(newParticipant);
        }
    }
}
