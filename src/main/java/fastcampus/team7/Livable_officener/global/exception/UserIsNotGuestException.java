package fastcampus.team7.Livable_officener.global.exception;

public class UserIsNotGuestException extends IllegalArgumentException {

    private static final String FORMAT = "'%s' 요청은 게스트만 가능합니다.";

    public UserIsNotGuestException(String requestName) {
        super(String.format(FORMAT, requestName));
    }
}
