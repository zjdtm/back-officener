package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateStoreDetailDTO {
    private String storeName;
    private String menuLink;
    private Long deliveryFee;
    private FoodTag foodTag;
    private BankName bankName;
    private String accountNum;
    private LocalDateTime deadline;
    private Long maxAttendees;
    private String desc;
}
