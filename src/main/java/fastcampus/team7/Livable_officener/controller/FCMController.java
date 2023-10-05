package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.fcm.FCMUpdateRequestDTO;
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
    public ResponseEntity<?> update(
            @RequestBody FCMUpdateRequestDTO dto,
            @AuthenticationPrincipal User user) {

        dto.setEmail(user.getEmail());
        fcmService.update(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/fcm-token")
    public ResponseEntity<?> unsubscribe(
            @AuthenticationPrincipal User user) {

        fcmService.unsubscribe(user.getEmail());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
