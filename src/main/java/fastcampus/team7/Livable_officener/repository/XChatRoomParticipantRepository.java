package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// TODO 함께배달 파트의 dao와 통합 필요
public interface XChatRoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    Optional<RoomParticipant> findByRoomIdAndUserId(Long roomId, Long userId);
}
