package fastcampus.team7.Livable_officener.global.exception;

public class AlreadyKickedException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "이미 추방된 회원 입니다.";

    public AlreadyKickedException() {
        this(DEFAULT_MESSAGE);
    }

    public AlreadyKickedException(String s) {
        super(s);
    }
}
