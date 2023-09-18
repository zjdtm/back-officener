package fastcampus.team7.Livable_officener.global.exception;

public class NotFoundRoomException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "해당 ID의 함께배달 방은 존재하지 않습니다.";

    public NotFoundRoomException() {
        this(DEFAULT_MESSAGE);
    }

    public NotFoundRoomException(String s) {
        super(s);
    }
}
