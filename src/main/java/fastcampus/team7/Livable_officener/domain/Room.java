package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.dto.UpdateStoreDetailDTO;
import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.AlreadyDeliveredException;
import fastcampus.team7.Livable_officener.global.exception.NotActiveRoomException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter @Setter
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
    @Enumerated(value = EnumType.STRING)
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

    private LocalDateTime deliveredAt;

    public void closeParticipation() {
        if (getStatus() != RoomStatus.ACTIVE) {
            throw new NotActiveRoomException();
        }
        status = RoomStatus.CLOSED;
    }

    public void completeDelivery() {
        if (getDeliveredAt() != null) {
            throw new AlreadyDeliveredException();
        }
        deliveredAt = LocalDateTime.now();
    }

    public void updateStoreDetail(UpdateStoreDetailDTO requestDTO) {
        this.storeName = requestDTO.getStoreName();
        this.menuLink = requestDTO.getMenuLink();
        this.deliveryFee = requestDTO.getDeliveryFee();
        this.tag = requestDTO.getFoodTag();
        this.bankName = requestDTO.getBankName();
        this.accountNumber = requestDTO.getAccountNum();
        this.deadline = requestDTO.getDeadline();
        this.maxAttendees = requestDTO.getMaxAttendees();
        this.description = requestDTO.getDesc();
    }
}
