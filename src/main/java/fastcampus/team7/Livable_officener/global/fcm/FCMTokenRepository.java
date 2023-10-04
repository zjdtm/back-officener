package fastcampus.team7.Livable_officener.global.fcm;

import fastcampus.team7.Livable_officener.dto.fcm.FCMRegistrationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class FCMTokenRepository {

    private final RedisTemplate<Long, String> fcmTokenRedisTemplate;

    public void save(FCMRegistrationDTO dto) {
        fcmTokenRedisTemplate.opsForValue()
                .set(dto.getUserId(), dto.getFcmToken());
    }

    public Optional<String> find(Long userId) {
        String fcmToken = fcmTokenRedisTemplate.opsForValue().get(userId);
        return Optional.ofNullable(fcmToken);
    }

    public void delete(Long userId) {
        fcmTokenRedisTemplate.delete(userId);
    }

    public boolean contains(Long userId) {
        return Boolean.TRUE.equals(fcmTokenRedisTemplate.hasKey(userId));
    }
}
