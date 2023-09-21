package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.dto.NotificationDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/api/notify/list")
    public ResponseEntity<APIDataResponse<List<NotificationDTO>>> getNotifyList(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        String token = authorization.split(" ")[1];
        return notificationService.getNotifyList(token);
    }


    @PostMapping("/api/notify/readAll")
    public ResponseEntity<APIDataResponse<String>> readAll(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){
        String token = authorization.split(" ")[1];
        return notificationService.readAll(token);
    }


}
