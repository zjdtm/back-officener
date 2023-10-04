package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Chat;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository {

    void save(Chat chat);

    List<Chat> findByRoomIdAndJoinedAtAfterOrderByCreatedAtDesc(Long roomId, LocalDateTime joinedAt);
}
