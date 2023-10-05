package fastcampus.team7.Livable_officener.global.fcm;

import fastcampus.team7.Livable_officener.dto.fcm.FCMUpdateRequestDTO;
import fastcampus.team7.Livable_officener.global.constant.FCMNotificationStatus;
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

    public boolean isActive(String email) {
        FCMNotificationStatus status = find(email);
        return status == FCMNotificationStatus.ACTIVE;
    }

    private FCMNotificationStatus find(String email) {
        String statusString = fcmNotificationStatusRedisTemplate.opsForValue().get(email);
        if (statusString == null) {
            throw new IllegalStateException("토큰은 존재하는데 알림 상태는 존재하지 않는 비정상 상태입니다.");
        }
        return FCMNotificationStatus.valueOf(statusString);
    }
}
