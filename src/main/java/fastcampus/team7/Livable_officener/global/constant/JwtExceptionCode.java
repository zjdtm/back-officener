package fastcampus.team7.Livable_officener.global.constant;

import lombok.Getter;

@Getter
public enum JwtExceptionCode {

    UNKNOWN_ERROR("알 수 없는 에러입니다."),
    WRONG_TYPE_TOKEN("잘못된 타입의 토큰입니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),
    UNSUPPORTED_TOKEN("지원되지 않는 토큰입니다."),
    ACCESS_DENIED("인증되지 않은 토큰입니다.");

    String message = "";

    JwtExceptionCode(String message) {
        this.message = message;
    }
}
