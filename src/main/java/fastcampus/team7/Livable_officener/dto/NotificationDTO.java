package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.domain.Notification;
import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.NotificationContent;
import fastcampus.team7.Livable_officener.global.constant.NotificationType;
import fastcampus.team7.Livable_officener.global.constant.SystemMessage;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class NotificationDTO {

    private Long receiverId;
    private Long roomId;
    private String content;
    private String type;
    private boolean isRead;
    private String menuTag;
    private Timestamp createdAt;

}
