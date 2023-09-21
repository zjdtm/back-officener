package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Report;
import fastcampus.team7.Livable_officener.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Optional<Report> findByReporterAndReportedUserAndCreatedAtIsAfter(User reporter, User reportedUser, LocalDateTime date);
}
