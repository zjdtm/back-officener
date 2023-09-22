package fastcampus.team7.Livable_officener.dto.chat;

import fastcampus.team7.Livable_officener.global.constant.ReportType;
import lombok.Getter;

import javax.validation.constraints.Size;

@Getter
public class ReportDTO {

    private ReportType reportType;

    @Size(max = 2000)
    private String reportMessage;

    private Long reportedUserId;

}
