package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Room, Long>, DeliveryRepositoryCustom{

    @Query("SELECT r FROM Room r JOIN RoomParticipant rp ON r.id = rp.room.id WHERE rp.user.id = :userId")
    Page<Room> findRoomsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT r FROM Room r WHERE r.deadline > CURRENT_TIMESTAMP")
    List<Room> findByDeadlineAfterNow();
}
