package fastcampus.team7.Livable_officener.global.exception;

public class NotVerifiedPhoneAuthCodeException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "잘못된 인증 코드입니다.";

    public NotVerifiedPhoneAuthCodeException() {
        super(DEFAULT_MESSAGE);
    }

    public NotVerifiedPhoneAuthCodeException(String s) {
        super(s);
    }

}
