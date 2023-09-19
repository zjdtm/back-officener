package fastcampus.team7.Livable_officener.global.exception;

public class NotFoundBuildingException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "존재하지 않는 건물명입니다.";

    public NotFoundBuildingException() {
        this(DEFAULT_MESSAGE);
    }

    public NotFoundBuildingException(String s) {
        super(s);
    }
}
