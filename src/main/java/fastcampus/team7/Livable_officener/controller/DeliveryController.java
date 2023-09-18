package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.dto.RoomDetailDTO;
import fastcampus.team7.Livable_officener.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findRoomDetail(@PathVariable Long id){
        final RoomDetailDTO roomDetail = deliveryService.selectRoomDetail(id);
        return ResponseEntity.ok(roomDetail);
    }
}
