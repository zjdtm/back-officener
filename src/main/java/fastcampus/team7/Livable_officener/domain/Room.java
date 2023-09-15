package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Entity
public class Room extends BaseEntity {

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private String menuLink;

    @Column(nullable = false)
    private Long deliveryFee;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private FoodTag tag;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String hostName;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false)
    private Long attendees;

    @Column(nullable = false)
    private Long maxAttendees;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RoomStatus status;
}
