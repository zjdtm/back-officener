package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Chat;

import java.util.List;

public interface ChatRepository {

    void save(Chat chat);

    List<Chat> findByRoomIdOrderByCreatedAtDesc(Long roomId);
}
