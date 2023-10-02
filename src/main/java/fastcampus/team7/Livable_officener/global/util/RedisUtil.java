package fastcampus.team7.Livable_officener.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, String> redisPhoneAuthCodeTemplate;
    private final RedisTemplate<String, String> redisBlackListTemplate;

    // 핸드폰 인증 코드 Redis 단
    public String setPhoneAuthCode(String key) {
        redisPhoneAuthCodeTemplate.opsForValue().set(key, generateVerifyCode(), 1, TimeUnit.MINUTES);
        return getPhoneAuthCode(key);
    }

    public String getPhoneAuthCode(String key) {
        return redisPhoneAuthCodeTemplate.opsForValue().get(key);
    }

    public boolean deletePhoneAuthCode(String key) {
        return Boolean.TRUE.equals(redisPhoneAuthCodeTemplate.delete(key));
    }

    public boolean hasPhoneAuthCode(String key) {
        return Boolean.TRUE.equals(redisPhoneAuthCodeTemplate.hasKey(key));
    }

    public String changePhoneAuthCode(String key) {

        if (hasPhoneAuthCode(key)) {
            redisPhoneAuthCodeTemplate.opsForValue().set(key, generateVerifyCode(), 1, TimeUnit.MINUTES);
        }

        return redisPhoneAuthCodeTemplate.opsForValue().get(key);

    }

    // 로그아웃 유저 관리 Redis 단
    public void setBlackList(String key, String value, Long minutes) {
        redisBlackListTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
    }

    public String getBlackList(String key) {
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.delete(key));
    }

    public boolean hasBlackList(String key) {
        return Boolean.TRUE.equals(redisBlackListTemplate.hasKey(key));
    }

    private String generateVerifyCode() {
        Random random = new Random();
        String newVerifyCode = "";
        for (int i = 0; i < 6; i++) {
            newVerifyCode += Integer.toString(random.nextInt(10));
        }
        return newVerifyCode;
    }

}
