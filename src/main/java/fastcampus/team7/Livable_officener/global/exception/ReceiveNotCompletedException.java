package fastcampus.team7.Livable_officener.global.exception;

public class ReceiveNotCompletedException extends IllegalStateException {

    public static final String DEFAULT_MESSAGE = "아직 수령완료가 안된 참가자가 있습니다.";

    public ReceiveNotCompletedException() {
        this(DEFAULT_MESSAGE);
    }

    public ReceiveNotCompletedException(String s) {
        super(s);
    }

}
