package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryParticipantRepository extends JpaRepository<RoomParticipant, Long>, DeliveryParticipantRepositoryCustom {

    void deleteAllByRoomId(Long roomId);

    List<RoomParticipant> findAllByRoomId(Long roomId);
}