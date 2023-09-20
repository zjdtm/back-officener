package fastcampus.team7.Livable_officener.global.exception;

public class AlreadyReceivedException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "이미 수령을 완료 했습니다.";

    public AlreadyReceivedException() {
        this(DEFAULT_MESSAGE);
    }

    public AlreadyReceivedException(String s) {
        super(s);
    }
}
