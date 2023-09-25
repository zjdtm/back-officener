package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.global.constant.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryParticipantRepository extends JpaRepository<RoomParticipant, Long>, DeliveryParticipantRepositoryCustom {

    void deleteAllByRoomId(Long roomId);

    List<RoomParticipant> findAllByRoomId(Long roomId);

    @Query("SELECT rp.user.id FROM RoomParticipant rp WHERE rp.room.id = :roomId AND rp.role = :role")
    Optional<Long> findUserIdByRoomIdAndRole(@Param("roomId") Long roomId, @Param("role") Role role);
}
