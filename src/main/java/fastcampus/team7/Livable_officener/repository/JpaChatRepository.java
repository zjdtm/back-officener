package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaChatRepository implements ChatRepository {

    private final EntityManager em;

    @Override
    public void save(Chat chat) {
        em.persist(chat);
    }

    @Override
    public List<Chat> findByRoomIdOrderByCreatedAtDesc(Long roomId) {
        final String qlStr =
                "SELECT c FROM Chat c " +
                "WHERE c.room.id = :roomId " +
                "ORDER BY c.createdAt DESC";
        return em.createQuery(qlStr, Chat.class)
                .setParameter("roomId", roomId)
                .getResultList();
    }
}
