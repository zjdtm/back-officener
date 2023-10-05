package fastcampus.team7.Livable_officener.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import fastcampus.team7.Livable_officener.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

@RequiredArgsConstructor
public enum ChatType {

    TALK("채팅", false, null, 0),
    ENTER("입장", true, "%s님이 함께배달에 입장했습니다.", 1),
    CLOSE_PARTICIPATION("참여마감", true, "%s님이 참여한 배달의 모집이 완료되었습니다.", 1),
    CLOSE_TO_DEADLINE("이체마감임박", true, "이체마감시간이 임박했어요. 빠른 송금 부탁드려요~", 0),
    COMPLETE_REMITTANCE("송금완료", true, "%s님이 송금을 완료했어요! 호스트님 확인해주세요.", 1),
    COMPLETE_DELIVERY("배달완료", true, "배달이 완료되었어요. 음식을 수령해주세요.", 0),
    COMPLETE_RECEIPT("수령완료", true, "%s님이 수령을 완료했어요!", 1),
    REQUEST_EXIT("나가기요청", true, "%s님이 나가기 요청을 했어요! 호스트님 확인해주세요.", 1),
    EXIT("나가기", true, "%s님이 채팅방에서 나갔습니다.", 1),
    KICK("강퇴", true, "%s님이 %s님을 강퇴 했습니다.", 2)
    ;

    private final String description;
    private final boolean isSystemMessage;
    private final String contentFormat;
    private final int requiredNumArgs;

    public String getSystemMessageContent(@Nullable User... args) {
        if (!isSystemMessage) {
            throw new IllegalCallerException("TALK is not a system message.");
        }

        int numArgs = (args == null) ? 0 : args.length;
        if (numArgs < requiredNumArgs) {
            throw new IllegalArgumentException("Insufficient arguments");
        }

        if (numArgs == 0) {
            return contentFormat;
        }

        Object[] copiedArgs = new Object[requiredNumArgs];
        System.arraycopy(args, 0, copiedArgs, 0, requiredNumArgs);
        return String.format(contentFormat, copiedArgs);
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ChatType from(String str) {
        return valueOf(str);
    }
}
