package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Notification;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.notification.NotificationDTO;
import fastcampus.team7.Livable_officener.global.exception.NotFoundUserException;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.NotificationRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public ResponseEntity<APIDataResponse<List<NotificationDTO>>> getNotifyList(String token){
        String email = jwtProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException());
        Long id = user.getId();
        List<Notification> notifications = notificationRepository.findByUserId(id)
                .orElse(null);
        System.out.println(notifications);


        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for(Notification notification : notifications){
            notificationDTOS.add(toDTO(notification));
        }

        ResponseEntity<APIDataResponse<List<NotificationDTO>>> responseEntity = APIDataResponse.of(
                HttpStatus.OK, notificationDTOS);

        return responseEntity;
    }

    @Transactional
    public ResponseEntity<APIDataResponse<String>> readAll(String token){
        String email = jwtProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundUserException());
        Long id = user.getId();

        List<Notification> notifications = notificationRepository.findByUserId(id)
                .orElse(null);

        for(Notification notification : notifications){
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        ResponseEntity<APIDataResponse<String>> responseEntity = APIDataResponse.empty(
                HttpStatus.OK);

        return responseEntity;
    }

    @Transactional
    public ResponseEntity<APIDataResponse<String>> readNotify(String token,Long notifyId){
            String email = jwtProvider.getEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundUserException());

            Long id = user.getId();

            Notification notification = notificationRepository.findById(notifyId)
                    .orElse(null);

            if (notification.getUser().getId() == id) {
                notification.setRead(true);
                notificationRepository.save(notification);
            } else {
                throw new RuntimeException("토큰 정보와 알림이 일치하지 않습니다.");
            }
        ResponseEntity<APIDataResponse<String>> responseEntity = APIDataResponse.empty(
                HttpStatus.OK);

        return responseEntity;
    }

        public NotificationDTO toDTO (Notification entity){
            NotificationDTO DTO = new NotificationDTO();
            DTO.setId(entity.getId());
            DTO.setReceiverId(entity.getUser().getId());
            DTO.setRoomId(entity.getRoom().getId());
            DTO.setContent(entity.getNotificationContent().getName());
            DTO.setType(entity.getNotificationType().getName());
            DTO.setRead(entity.isRead());
            DTO.setMenuTag(entity.getFoodTag().toString());
            DTO.setCreatedAt(entity.getCreatedAt());
            return DTO;
        }
}
