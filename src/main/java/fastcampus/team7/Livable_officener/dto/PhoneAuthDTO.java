package fastcampus.team7.Livable_officener.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@Builder
@RedisHash(timeToLive = 60)
public class PhoneAuthDTO implements Serializable {

    @Id
    private String phoneNumber;

    private String verifyCode;

    public void changeVerifyCode(String newVerifyCode) {
        this.verifyCode = newVerifyCode;
    }
}
