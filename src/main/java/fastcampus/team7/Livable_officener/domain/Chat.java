package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.dto.SendChatDTO;
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

    private String content;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ChatType type;

    public static Chat from(SendChatDTO dto) {
        return new Chat(dto.getRoom(),
                dto.getSender(),
                dto.getMessage().getPayload(),
                dto.getType());
    }
}
