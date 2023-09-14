package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Room;

import java.util.Optional;

// TODO 함께배달 파트의 dao와 통합 필요
public interface ChatRoomRepository {
    Optional<Room> findById(Long id);
}
