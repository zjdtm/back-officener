package fastcampus.team7.Livable_officener.dto.fcm;

import fastcampus.team7.Livable_officener.global.constant.FCMNotificationStatus;
import fastcampus.team7.Livable_officener.global.constant.FCMNotificationStatusUpdateType;
import lombok.Getter;

@Getter
public class FCMUpdateRequestDTO {

    private String email;
    private String fcmToken;
    private FCMNotificationStatusUpdateType status;

    public void setEmail(String email) {
        this.email = email;
    }

    public String convertStatusString() {
        FCMNotificationStatus fcmNotificationStatus;
        if (status == FCMNotificationStatusUpdateType.ACTIVATE) {
            fcmNotificationStatus = FCMNotificationStatus.ACTIVE;
        } else if (status == FCMNotificationStatusUpdateType.DEACTIVATE) {
            fcmNotificationStatus = FCMNotificationStatus.INACTIVE;
        } else {
            throw new IllegalCallerException("KEEP은 statusString으로 변환할 수 없습니다.");
        }
        return fcmNotificationStatus.name();
    }
}
