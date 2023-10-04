package fastcampus.team7.Livable_officener.dto.fcm;

import com.google.firebase.messaging.Notification;
import lombok.Getter;

@Getter
public class FCMNotificationDTO {

    private static final String TITLE = "함께배달";

    private Long receiverId;
    private final String title;
    private final String body;
    private final String image;

    public FCMNotificationDTO(String body, String image) {
        this(null, TITLE, body, image);
    }

    public FCMNotificationDTO(Long receiverId, String body, String image) {
        this(receiverId, TITLE, body, image);
    }

    public FCMNotificationDTO(Long receiverId, String title, String body, String image) {
        this.receiverId = receiverId;
        this.title = title;
        this.body = body;
        this.image = image;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Notification makeNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(image)
                .build();
    }
}
