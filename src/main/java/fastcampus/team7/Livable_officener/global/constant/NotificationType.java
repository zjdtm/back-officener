package fastcampus.team7.Livable_officener.global.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NotificationType {
    DELIVERED("배달완료"),
    TRANSFERRED("송금완료"),
    COMPLETE_RECEIPT("수령완료"),
    REQUEST_EXIT("나가기요청"),
    KICK("강퇴")

    ;

    private final String name;

    public String getName() {
        return name;
    }
}
