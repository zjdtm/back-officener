package fastcampus.team7.Livable_officener.global.exception;

public class DuplicatedPhoneNumberException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "이미 등록된 휴대폰 번호입니다.";

    public DuplicatedPhoneNumberException() {
        this(DEFAULT_MESSAGE);
    }

    public DuplicatedPhoneNumberException(String s) {
        super(s);
    }
}
