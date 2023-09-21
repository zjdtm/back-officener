package fastcampus.team7.Livable_officener.global.exception;

public class RemitNotCompletedException extends IllegalStateException{

    public static final String DEFAULT_MESSAGE = "아직 송금완료가 안된 참가자가 있습니다.";

    public RemitNotCompletedException() {
        this(DEFAULT_MESSAGE);
    }

    public RemitNotCompletedException(String s) {
        super(s);
    }

}
