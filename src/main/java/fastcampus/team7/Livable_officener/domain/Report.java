package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.ReportType;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
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

    public void assignReportedUser(User user) {
        this.reportedUser = user;
    }

    public void assignReporter(User user) {
        this.reporter = user;
    }

    public void assignType(ReportType type) {
        this.type = type;
    }

    public void assignReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }
}
