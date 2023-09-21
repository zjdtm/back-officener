package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.dto.chat.ReportDTO;
import fastcampus.team7.Livable_officener.global.constant.ReportType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@RequiredArgsConstructor
public class Report extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private User reporter;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReportType type;

    @Column(length = 2000)
    @Lob
    private String reportMessage;

    public static Report createReport(ReportDTO reportDTO, User reportedUser, User reporter) {
        return Report.builder()
                .reportedUser(reportedUser)
                .reporter(reporter)
                .type(reportDTO.getReportType())
                .reportMessage(reportDTO.getReportMessage())
                .build();
    }
}
