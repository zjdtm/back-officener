package fastcampus.team7.Livable_officener.global.constant;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public enum SystemMessage {
    CLOSE_PARTICIPATION("%s님이 참여한 배달의 모집이 완료되었습니다.", 1),
    COMPLETE_REMIT("%s님이 송금을 완료했어요! 호스트님 확인해주세요.", 1),
    COMPLETE_DELIVERY("배달이 완료되었어요. 음식을 수령해주세요.", 0),
    COMPLETE_RECEIVE("%s님이 수령을 완료했어요!", 1),
    ;

    private final String contentFormat;
    private final int requiredNumArgs;

    public String getContent(@Nullable Object... args) {
        int numArgs = (args == null) ? 0 : args.length;
        if (numArgs != requiredNumArgs) {
            throw new IllegalArgumentException("Unmatched the number of arguments");
        }
        return String.format(contentFormat, args);
    }
}
