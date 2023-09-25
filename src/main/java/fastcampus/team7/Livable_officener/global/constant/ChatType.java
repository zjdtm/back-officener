package fastcampus.team7.Livable_officener.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public enum ChatType {

    TALK(false, null, 0),
    CLOSE_PARTICIPATION(true, "%s님이 참여한 배달의 모집이 완료되었습니다.", 1),
    COMPLETE_REMITTANCE(true, "%s님이 송금을 완료했어요! 호스트님 확인해주세요.", 1),
    COMPLETE_DELIVERY(true, "배달이 완료되었어요. 음식을 수령해주세요.", 0),
    COMPLETE_RECEIPT(true, "%s님이 수령을 완료했어요!", 1),
    REQUEST_EXIT(true, "%s님이 나가기 요청을 했어요! 호스트님 확인해주세요", 1),
    EXIT(true,"%s님이 채팅방에서 나갔습니다.",1),
    KICK(true,"%s님이 %s님을 강퇴 했습니다.",2)
    ;


    private final boolean isSystemMessage;
    private final String contentFormat;
    private final int requiredNumArgs;

    public String getSystemMessageContent(@Nullable Object... args) {
        if (!isSystemMessage) {
            throw new IllegalCallerException("TALK is not a system message.");
        }
        int numArgs = (args == null) ? 0 : args.length;
        if (numArgs != requiredNumArgs) {
            throw new IllegalArgumentException("Unmatched the number of arguments");
        }
        return String.format(contentFormat, args);
    }

    @JsonCreator
    public static ChatType from(String str) {
        return valueOf(str);
    }
}
