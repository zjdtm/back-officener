package fastcampus.team7.Livable_officener.dto.fcm;

import com.google.firebase.messaging.Notification;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import lombok.Getter;

@Getter
public class FCMNotificationDTO {

    private static final String TITLE = "함께배달";

    private String receiverEmail;
    private final String title;
    private String body;
    private final String image;

    public FCMNotificationDTO(String image) {
        this(null, TITLE, null, image);
    }

    public FCMNotificationDTO(String receiverEmail, String body, String image) {
        this(receiverEmail, TITLE, body, image);
    }

    public FCMNotificationDTO(String receiverEmail, String title, String body, String image) {
        this.receiverEmail = receiverEmail;
        this.title = title;
        this.body = body;
        this.image = image;
    }

    public void setReceiverEmailAndBody(ChatType type, User guest) {
        receiverEmail = guest.getEmail();
        body = type.getSystemMessageContent(guest);
    }

    public Notification makeNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .setImage(image)
                .build();
    }
}
