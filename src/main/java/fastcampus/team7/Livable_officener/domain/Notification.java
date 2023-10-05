package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ChatType type;

    @Column
    private boolean isRead;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FoodTag foodTag;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private String content;

    public Notification(Room room, ChatType type, User user) {
        this.user = user;
        this.room = room;
        this.type = type;
        foodTag = room.getTag();
        createdAt = Timestamp.valueOf(LocalDateTime.now());
        content = type.getSystemMessageContent(user);
    }
}
