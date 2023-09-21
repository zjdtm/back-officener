package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Notification;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.NotificationDTO;
import fastcampus.team7.Livable_officener.global.sercurity.JwtProvider;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.NotificationRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public ResponseEntity<APIDataResponse<List<NotificationDTO>>> getNotifyList(String token){
        String email = jwtProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당하는 유저가 없습니다."));;
        Long id = user.getId();
        List<Notification> notifications = notificationRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("해당하는 유저에 알림이 없습니다."));

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for(Notification notification : notifications){
            notificationDTOS.add(toDTO(notification));
        }

        ResponseEntity<APIDataResponse<List<NotificationDTO>>> responseEntity = APIDataResponse.of(
                HttpStatus.OK, notificationDTOS);

        return responseEntity;
    }

    public ResponseEntity<APIDataResponse<String>> readAll(String token){
        String email = jwtProvider.getEmail(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("해당하는 유저가 없습니다."));;
        Long id = user.getId();

        List<Notification> notifications = notificationRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("해당하는 유저에 알림이 없습니다."));

        for(Notification notification : notifications){
            notification.setRead(true);
            notificationRepository.save(notification);
        }

        ResponseEntity<APIDataResponse<String>> responseEntity = APIDataResponse.empty(
                HttpStatus.OK);

        return responseEntity;
    }

    public NotificationDTO toDTO(Notification entity){
        NotificationDTO DTO = new NotificationDTO();
        DTO.setReceiverId(entity.getUser().getId());
        DTO.setRoomId(entity.getRoom().getId());
        DTO.setContent(entity.getNotificationContent().getName());
        DTO.setType(entity.getNotificationType().getName());
        DTO.setRead(entity.isRead());
        DTO.setMenuTag(entity.getFoodTag().getName());
        DTO.setCreatedAt(entity.getCreatedAt());
        return DTO;
    }
}
