package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.dto.notification.NotificationDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/api/notify")
    public ResponseEntity<APIDataResponse<List<NotificationDTO>>> getNotifyList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.split(" ")[1];
        return notificationService.getNotifyList(token);
    }

    @PostMapping("/api/notify/readAll")
    public ResponseEntity<APIDataResponse<String>> readAll(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.split(" ")[1];
        return notificationService.readAll(token);
    }

    @PostMapping("/api/notify/{notifyId}")
    public ResponseEntity<APIDataResponse<String>> readNotify(
            @PathVariable Long notifyId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.split(" ")[1];
        return notificationService.readNotify(token,notifyId);
    }
}
