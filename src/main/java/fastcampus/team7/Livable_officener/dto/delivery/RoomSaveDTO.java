package fastcampus.team7.Livable_officener.dto.delivery;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoomSaveDTO {
    private String storeName;
    private String menuLink;
    private Long deliveryFee;
    private FoodTag foodTag;
    private BankName bankName;
    private String accountNumber;
    private String hostName;
    private LocalDateTime deadline;
    private Long attendees;
    private Long maxAttendees;
    private String desc;
    private RoomStatus status;

    public Room toEntity() {
        return Room.builder()
                .storeName(storeName)
                .menuLink(menuLink)
                .deliveryFee(deliveryFee)
                .tag(foodTag)
                .bankName(bankName)
                .accountNumber(accountNumber)
                .hostName(hostName)
                .deadline(deadline)
                .attendees(attendees)
                .maxAttendees(maxAttendees)
                .description(desc)
                .status(status)
                .build();
    }
}
