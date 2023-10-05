package fastcampus.team7.Livable_officener.global.config;

import fastcampus.team7.Livable_officener.domain.Notification;
import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.fcm.FCMNotificationDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.exception.NotFoundUserException;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import fastcampus.team7.Livable_officener.repository.NotificationRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import fastcampus.team7.Livable_officener.service.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class SchedulingConfig {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void closeParticipationAfterDeadline() {
        deliveryRepository.findByDeadlineAfterNow()
                .forEach(Room::closeParticipation);
    }

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void pushToGuests5MinBeforeDeadline() {
        // deadline - 5 <= now => deadline <= now + 5
        final LocalDateTime fiveMinutesAfter = LocalDateTime.now().plusMinutes(5);
        final ChatType messageType = ChatType.CLOSE_TO_DEADLINE;
        List<Room> rooms = deliveryRepository.findByDeadlineBefore(fiveMinutesAfter);
        for (Room room : rooms) {
            // 모든 게스트에게 (시스템 메시지 및) 알림 송신, 알림 저장
            List<Long> guestIds = participantRepository.findUserIdsByRoomIdAndRole(room.getId(), Role.GUEST);
            FCMNotificationDTO fcmNotificationDTO = new FCMNotificationDTO(null);
            for (Long guestId : guestIds) {
                User guest = getUser(guestId);

                saveNotification(messageType, room, guest);

                pushNotificationToGuestIfSubscribed(messageType, fcmNotificationDTO, guest);
            }
        }
    }

    private void saveNotification(ChatType messageType, Room room, User guest) {
        Notification notification = new Notification(room, messageType, guest);
        notificationRepository.save(notification);
    }

    private void pushNotificationToGuestIfSubscribed(ChatType messageType, FCMNotificationDTO dto, User guest) {
        if (fcmService.isSubscribed(guest.getEmail())) {
            dto.setReceiverEmailAndBody(messageType, guest);
            fcmService.sendFcmNotification(dto);
        }
    }

    private User getUser(Long guestId) {
        return userRepository.findById(guestId)
                .orElseThrow(NotFoundUserException::new);
    }
}
