package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.NotificationContent;
import fastcampus.team7.Livable_officener.global.constant.NotificationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

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
}
