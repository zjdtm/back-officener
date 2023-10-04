package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.NotificationContent;
import fastcampus.team7.Livable_officener.global.constant.NotificationType;
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
    private NotificationType notificationType;

    @Column
    private boolean isRead;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FoodTag foodTag;

    @Column(nullable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private NotificationContent notificationContent;

    public Notification(User user, Room room, NotificationType type) {
        this.user = user;
        this.room = room;
        this.notificationType = type;
        foodTag = room.getTag();
        createdAt = Timestamp.valueOf(LocalDateTime.now());
        notificationContent = type.getContent();
    }
}
