package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
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
    private String bankName;
    private String account;
    private LocalDateTime deadline;
    private Long attendees;
    private Long maxAttendees;
    private String description;
    private Boolean isJoin;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}

