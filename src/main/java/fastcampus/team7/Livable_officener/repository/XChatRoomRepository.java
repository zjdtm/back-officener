package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// TODO 함께배달 파트의 dao와 통합 필요
public interface XChatRoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findById(Long id);
}
