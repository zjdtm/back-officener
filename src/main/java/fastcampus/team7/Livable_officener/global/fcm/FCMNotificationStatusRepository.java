package fastcampus.team7.Livable_officener.global.fcm;

import fastcampus.team7.Livable_officener.dto.fcm.FCMUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FCMNotificationStatusRepository {

    private final StringRedisTemplate fcmNotificationStatusRedisTemplate;

    public void save(FCMUpdateRequestDTO dto) {
        fcmNotificationStatusRedisTemplate.opsForValue()
                .set(dto.getEmail(), dto.convertStatusString());
    }
}
