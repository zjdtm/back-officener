package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.dto.chat.SendPayloadDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Entity
public class Chat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ChatType type;

    private String content;

    public static Chat from(Room room, User sender, SendPayloadDTO sendPayloadDTO) {
        Chat chat = new Chat(
                room,
                sender,
                sendPayloadDTO.getMessageType(),
                sendPayloadDTO.getContent());
        chat.setCreatedAt(sendPayloadDTO.getSendTime());
        return chat;
    }
}
