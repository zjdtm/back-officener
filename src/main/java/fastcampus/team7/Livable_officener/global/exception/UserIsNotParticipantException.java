package fastcampus.team7.Livable_officener.global.exception;

public class UserIsNotParticipantException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "요청된 회원은 해당 ID의 함께배달 방의 참여자가 아닙니다.";

    public UserIsNotParticipantException() {
        this(DEFAULT_MESSAGE);
    }

    public UserIsNotParticipantException(String s) {
        super(s);
    }
}
