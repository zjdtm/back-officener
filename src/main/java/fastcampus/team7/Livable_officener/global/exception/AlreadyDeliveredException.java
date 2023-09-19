package fastcampus.team7.Livable_officener.global.exception;

public class AlreadyDeliveredException extends IllegalStateException {

    public static final String DEFAULT_MESSAGE = "이미 배달이 완료되었습니다.";

    public AlreadyDeliveredException() {
        this(DEFAULT_MESSAGE);
    }

    public AlreadyDeliveredException(String s) {
        super(s);
    }

}
