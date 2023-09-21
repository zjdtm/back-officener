package fastcampus.team7.Livable_officener.global.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NotificationContent {
    DELIVERED("배달이 완료되었습니다. 찾아가주세요"),
    TRANSFERRED("송금완료이 완료되었습니다. 확인해주세요"),
    KICK("강퇴당했습니다. 확인해주세요.")
    ;

    private final String name;

    public String getName() {
        return name;
    }
}
