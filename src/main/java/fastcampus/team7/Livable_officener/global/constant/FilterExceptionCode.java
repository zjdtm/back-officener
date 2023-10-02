package fastcampus.team7.Livable_officener.global.constant;

import lombok.Getter;

@Getter
public enum FilterExceptionCode {

    UNKNOWN_ERROR("알 수 없는 에러입니다."),
    WRONG_TYPE_TOKEN("잘못된 타입의 토큰입니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),
    UNSUPPORTED_TOKEN("지원되지 않는 토큰입니다."),
    ACCESS_DENIED("인증되지 않은 토큰입니다."),
    ALREADY_LOGGED_OUT("이미 로그아웃 된 상태입니다. 다시 로그인 해주세요.");

    String message = "";

    FilterExceptionCode(String message) {
        this.message = message;
    }
}
