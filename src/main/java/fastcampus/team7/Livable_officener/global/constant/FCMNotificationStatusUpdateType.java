package fastcampus.team7.Livable_officener.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum FCMNotificationStatusUpdateType {

    ACTIVATE,
    DEACTIVATE,
    KEEP

    ;

    @JsonCreator
    public static FCMNotificationStatusUpdateType from(String status) {
        return valueOf(status);
    }
}
