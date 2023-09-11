package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.ReportType;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Report extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private User reportedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private User reporter;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReportType type;

}
