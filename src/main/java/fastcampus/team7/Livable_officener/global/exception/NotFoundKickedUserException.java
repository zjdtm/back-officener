package fastcampus.team7.Livable_officener.global.exception;

public class NotFoundKickedUserException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "해당 아이디의 강퇴 대상자가 없습니다.";

    public NotFoundKickedUserException() {
        this(DEFAULT_MESSAGE);
    }

    public NotFoundKickedUserException(String s) {
        super(s);
    }
}
