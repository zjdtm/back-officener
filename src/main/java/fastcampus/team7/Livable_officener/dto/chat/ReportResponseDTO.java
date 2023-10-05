package fastcampus.team7.Livable_officener.dto.chat;

import fastcampus.team7.Livable_officener.domain.Report;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponseDTO {
    private String reportType;
    private String reportMessage;
    private Long reportedUserId;

    public static ReportResponseDTO fromEntity(Report report) {
        return ReportResponseDTO.builder()
                .reportType(report.getType().name())
                .reportMessage(report.getReportMessage())
                .reportedUserId(report.getReportedUser().getId())
                .build();
    }
}