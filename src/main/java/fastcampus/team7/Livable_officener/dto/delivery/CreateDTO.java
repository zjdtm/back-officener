package fastcampus.team7.Livable_officener.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CreateDTO {
    private String storeName;
    private String menuLink;
    private Long deliveryFee;
    private String foodTag;
    private String bankName;
    private String accountNumber;
    private LocalDateTime deadline;
    private Long maxAttendees;
    private String desc;
}
