package fastcampus.team7.Livable_officener.global.exception;

public class NotFoundReporterException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "해당 아이디의 신고자가 없습니다.";

    public NotFoundReporterException() {
        this(DEFAULT_MESSAGE);
    }

    public NotFoundReporterException(String s) {
        super(s);
    }
}
