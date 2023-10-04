package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.fcm.FCMRegistrationDTO;
import fastcampus.team7.Livable_officener.service.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/notify")
@RestController
public class FCMController {

    private final FCMService fcmService;

    @PostMapping("/fcm-token")
    public ResponseEntity<?> registerFcmToken(
            @RequestBody String fcmToken,
            @AuthenticationPrincipal User user) {

        FCMRegistrationDTO dto = new FCMRegistrationDTO(user.getId(), fcmToken);
        fcmService.registerFcmToken(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/fcm-token")
    public ResponseEntity<?> unsubscribe(
            @AuthenticationPrincipal User user) {

        fcmService.unsubscribe(user.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
