package fastcampus.team7.Livable_officener.global.exception;

public class AlreadyReportSameUserException extends IllegalStateException {

    public static final String DEFAULT_MESSAGE = "오늘 이미 해당 사용자를 신고하셨습니다.";

    public AlreadyReportSameUserException() {
        this(DEFAULT_MESSAGE);
    }

    public AlreadyReportSameUserException(String s) {
        super(s);
    }
}
