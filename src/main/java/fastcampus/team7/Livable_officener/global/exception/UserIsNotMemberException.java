package fastcampus.team7.Livable_officener.global.exception;

public class UserIsNotMemberException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "요청된 회원은 해당 ID의 함께배달 방의 참여자가 아닙니다.";

    public UserIsNotMemberException() {
        this(DEFAULT_MESSAGE);
    }

    public UserIsNotMemberException(String s) {
        super(s);
    }
}
