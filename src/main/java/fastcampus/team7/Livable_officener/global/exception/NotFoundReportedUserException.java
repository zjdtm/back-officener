package fastcampus.team7.Livable_officener.global.exception;

public class NotFoundReportedUserException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "해당 아이디의 피신고자가 없습니다.";

    public NotFoundReportedUserException() {
        this(DEFAULT_MESSAGE);
    }

    public NotFoundReportedUserException(String s) {
        super(s);
    }
}
