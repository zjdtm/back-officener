package fastcampus.team7.Livable_officener.global.exception;

public class NotFoundUserException extends RuntimeException{

    public static final String DEFAULT_MESSAGE = "존재하지 않는 회원입니다.";

    public NotFoundUserException() {
        super(DEFAULT_MESSAGE);
    }

    public NotFoundUserException(String username) {
        super(DEFAULT_MESSAGE + " username: " + username);
    }

}
