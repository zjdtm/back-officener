package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.domain.Report;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.ChatroomInfoDTO;
import fastcampus.team7.Livable_officener.dto.chat.KickDTO;
import fastcampus.team7.Livable_officener.dto.chat.ReportDTO;
import fastcampus.team7.Livable_officener.dto.chat.ReportResponseDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.global.util.APIErrorResponse;
import fastcampus.team7.Livable_officener.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api/chat/{roomId}")
@RestController
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/connect")
    public ResponseEntity<?> getChatroomInfo(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) {

        ChatroomInfoDTO dto = chatService.getChatroomInfo(roomId, user);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/enter")
    public ResponseEntity<?> enterChatroom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.enterChatroom(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/closed")
    public ResponseEntity<?> closeParticipation(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.closeParticipation(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/remitted")
    public ResponseEntity<?> completeRemit(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.completeRemit(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/delivered")
    public ResponseEntity<?> completeDelivery(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.completeDelivery(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/received")
    public ResponseEntity<?> completeReceive(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.completeReceive(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/exit")
    public ResponseEntity<?> exitChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.exitChatRoom(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/kickRequest")
    public ResponseEntity<?> kickRequest(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user) throws IOException {

        chatService.kickRequest(roomId, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/kick")
    public ResponseEntity<?> kick(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user,
            @RequestBody KickDTO kickDTO) throws IOException {

        chatService.kick(roomId, user, kickDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/report")
    public ResponseEntity<?> createReport(
            @PathVariable Long roomId,
            @AuthenticationPrincipal User user,
            @RequestBody ReportDTO reportDTO) {

        if (reportDTO.getReportedUserId().equals(user.getId())) {
            return APIErrorResponse.of(HttpStatus.BAD_REQUEST, "자기 자신을 신고할 수는 없습니다.");
        }

        Report report = chatService.createReport(roomId, user, reportDTO);
        ReportResponseDTO responseDTO = ReportResponseDTO.fromEntity(report);
        return APIDataResponse.of(HttpStatus.CREATED, responseDTO);
    }
}
