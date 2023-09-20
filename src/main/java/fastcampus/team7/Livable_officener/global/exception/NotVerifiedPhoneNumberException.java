package fastcampus.team7.Livable_officener.global.exception;

public class NotVerifiedPhoneNumberException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "인증되지 않은 핸드폰 번호입니다.";

    public NotVerifiedPhoneNumberException() {
        this(DEFAULT_MESSAGE);
    }

    public NotVerifiedPhoneNumberException(String s) {
        super(s);
    }

}
