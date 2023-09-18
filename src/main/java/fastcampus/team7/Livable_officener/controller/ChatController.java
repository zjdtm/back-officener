package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api/chat/{roomId}")
@RestController
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/closed")
    public ResponseEntity<?> closeParticipation(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.closeParticipation(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/transferred")
    public ResponseEntity<?> completeTransfer(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.completeTransfer(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
