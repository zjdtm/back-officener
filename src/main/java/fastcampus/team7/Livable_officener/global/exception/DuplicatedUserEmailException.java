package fastcampus.team7.Livable_officener.global.exception;

public class DuplicatedUserEmailException extends IllegalArgumentException {

    public static final String DEFAULT_MESSAGE = "이미 존재하는 회원입니다.";

    public DuplicatedUserEmailException() {
        this(DEFAULT_MESSAGE);
    }

    public DuplicatedUserEmailException(String s) {
        super(s);
    }


}
