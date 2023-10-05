package fastcampus.team7.Livable_officener.dto.chat;

import fastcampus.team7.Livable_officener.global.constant.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReportDTO {

    private ReportType reportType;

    private String reportMessage;

    private Long reportedUserId;

}
