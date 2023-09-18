package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
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
    private BankName bankName;

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
