package fastcampus.team7.Livable_officener.global.exception;

public class InvalidPasswordException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "패스워드가 일치하지 않습니다.";

    public InvalidPasswordException() {
        this(DEFAULT_MESSAGE);
    }

    public InvalidPasswordException(String s) {
        super(s);
    }
}
