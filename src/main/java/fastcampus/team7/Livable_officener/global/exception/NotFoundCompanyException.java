package fastcampus.team7.Livable_officener.global.exception;

public class NotFoundCompanyException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "해당 이름의 회사를 찾을 수 없습니다.";

    public NotFoundCompanyException() {
        this(DEFAULT_MESSAGE);
    }

    public NotFoundCompanyException(String s) {
        super(s);
    }

}
