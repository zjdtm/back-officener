package fastcampus.team7.Livable_officener.global.exception;

public class NotActiveRoomException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "해당 ID의 함께배달 방은 활성 상태가 아닙니다.";

    public NotActiveRoomException() {
        this(DEFAULT_MESSAGE);
    }

    public NotActiveRoomException(String s) {
        super(s);
    }
}
