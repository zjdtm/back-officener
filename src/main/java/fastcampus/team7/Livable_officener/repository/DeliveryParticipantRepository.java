package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.global.constant.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryParticipantRepository extends JpaRepository<RoomParticipant, Long>, DeliveryParticipantRepositoryCustom {

    List<RoomParticipant> findAllByRoomId(Long roomId);

    @Query("SELECT rp.user.id FROM RoomParticipant rp WHERE rp.room.id = :roomId AND rp.role = :role")
    List<Long> findUserIdsByRoomIdAndRole(@Param("roomId") Long roomId, @Param("role") Role role);
}
