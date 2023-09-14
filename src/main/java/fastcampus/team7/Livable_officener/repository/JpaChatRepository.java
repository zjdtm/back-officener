package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@RequiredArgsConstructor
@Repository
public class JpaChatRepository implements ChatRepository {

    private final EntityManager em;

    @Override
    public void save(Chat chat) {
        em.persist(chat);
    }
}
