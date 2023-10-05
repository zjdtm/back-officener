package fastcampus.team7.Livable_officener.global.fcm;

import fastcampus.team7.Livable_officener.dto.fcm.FCMSubscribeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FCMTokenRepository {

    private final StringRedisTemplate fcmTokenRedisTemplate;

    public void save(FCMSubscribeDTO dto) {
        fcmTokenRedisTemplate.opsForValue()
                .set(dto.getEmail(), dto.getFcmToken());
    }

    public Optional<String> find(String email) {
        String fcmToken = fcmTokenRedisTemplate.opsForValue().get(email);
        return Optional.ofNullable(fcmToken);
    }

    public void delete(String email) {
        fcmTokenRedisTemplate.delete(email);
    }

    public boolean contains(String email) {
        return Boolean.TRUE.equals(fcmTokenRedisTemplate.hasKey(email));
    }
}
