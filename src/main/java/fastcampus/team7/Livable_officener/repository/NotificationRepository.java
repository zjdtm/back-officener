package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query("select n from Notification n WHERE n.user.id = :id")
    Optional<List<Notification>> findByUserId(Long id);
}
