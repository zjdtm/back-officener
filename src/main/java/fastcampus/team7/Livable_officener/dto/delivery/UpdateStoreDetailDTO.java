package fastcampus.team7.Livable_officener.dto.delivery;

import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateStoreDetailDTO {

    @NotBlank(message = "가게이름을 입력해주세요")
    private String storeName;

    @NotBlank(message = "메뉴판 링크를 입력해주세요")
    private String menuLink;

    @NotNull(message = "배달비를 입력해주세요")
    private Long deliveryFee;

    @NotNull(message = "음식 태그를 선택해주세요")
    private FoodTag foodTag;

    @NotNull(message = "은행 정보를 입력해주세요")
    private BankName bankName;

    @NotBlank(message = "계좌 정보를 입력해주세요")
    private String accountNumber;

    @NotNull(message = "이체 마감시간을 입력해주세요")
    private LocalDateTime deadline;

    @Range(min = 2, max = 10)
    @NotNull(message = "최대참여인원을 선택해주세요.")
    private Long maxAttendees;
    private String desc;
}
