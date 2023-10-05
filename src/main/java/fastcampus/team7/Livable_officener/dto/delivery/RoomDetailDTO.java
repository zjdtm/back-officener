package fastcampus.team7.Livable_officener.dto.delivery;

import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RoomDetailDTO {
    private Long roomId;
    private Long hostId;
    private String hostName;
    private String storeName;
    private String menuLink;
    private Long deliveryFee;
    private FoodTag tag;
    private BankName bankName;
    private String account;
    private LocalDateTime deadline;
    private Long attendees;
    private Long maxAttendees;
    private String description;
    private RoomStatus status;
    private Boolean isJoin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

