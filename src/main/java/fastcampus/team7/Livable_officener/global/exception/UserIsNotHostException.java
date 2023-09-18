package fastcampus.team7.Livable_officener.global.exception;

public class UserIsNotHostException extends IllegalArgumentException {

    private static final String FORMAT = "'%s' 요청은 호스트만 가능합니다.";

    public UserIsNotHostException(String requestName) {
        super(String.format(FORMAT, requestName));
    }
}
