package fastcampus.team7.Livable_officener.dto;

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

    @Data
    public static class EmptyDTO{
        private String message = "성공했습니다.";
    }

}
