package fastcampus.team7.Livable_officener.global.exception;

public class AlreadyTransferredException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "이미 송금을 완료 했습니다.";

    public AlreadyTransferredException() {
        this(DEFAULT_MESSAGE);
    }

    public AlreadyTransferredException(String s) {
        super(s);
    }
}
