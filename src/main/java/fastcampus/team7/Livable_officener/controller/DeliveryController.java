package fastcampus.team7.Livable_officener.controller;


import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.dto.delivery.BankListResponseDTO;
import fastcampus.team7.Livable_officener.dto.delivery.CreateDTO;
import fastcampus.team7.Livable_officener.dto.delivery.PagedRoomListResponseDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findRoomDetail(@PathVariable Long id) {
        final RoomDetailDTO response = deliveryService.selectRoomDetail(id);
        return APIDataResponse.of(HttpStatus.OK, response);
    }

    @GetMapping("/bankList")
    public ResponseEntity<APIDataResponse<BankListResponseDTO>> bankList() {
        BankListResponseDTO response = deliveryService.loadBankList();
        return APIDataResponse.of(HttpStatus.OK, response);
    }

    @PostMapping("/create")
    public ResponseEntity<APIDataResponse<String>> create(
            @RequestBody CreateDTO createDTO,
            @AuthenticationPrincipal User user) {

        deliveryService.registerRoom(createDTO, user);

        return APIDataResponse.empty(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyStoreDetail(
            @PathVariable Long id,
            @RequestBody UpdateStoreDetailDTO requestDTO,
            @AuthenticationPrincipal User user) {
        deliveryService.updateStoreDetail(id, requestDTO, user);

        return APIDataResponse.empty(HttpStatus.OK);
    }

    @PostMapping("{id}/terminate")
    public ResponseEntity<?> deleteDelivery(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        deliveryService.deleteDelivery(id, user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<APIDataResponse<PagedRoomListResponseDTO>> list(@RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        PagedRoomListResponseDTO response = deliveryService.getRoomList(PageRequest.of(page, size));
        return APIDataResponse.of(HttpStatus.CREATED, response);
    }

    @GetMapping("/joinedRoom")
    public ResponseEntity<APIDataResponse<PagedRoomListResponseDTO>> joinedRoom(@RequestParam(defaultValue = "0") int page,
                                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                                    @AuthenticationPrincipal User user) {

        PagedRoomListResponseDTO response = deliveryService.getFilteredRoomList(PageRequest.of(page, size), user);
        return APIDataResponse.of(HttpStatus.CREATED, response);
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<APIDataResponse<String>> join(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        deliveryService.joinDeliveryRoom(id, user);
        return APIDataResponse.empty(HttpStatus.CREATED);
    }

    @GetMapping("/chats")
    public ResponseEntity<APIDataResponse<ChatRoomListResponseDTO.MyChatListResponseDTO>> chatRoomList(@AuthenticationPrincipal User user) {
        return APIDataResponse.of(HttpStatus.OK, deliveryService.getChatRoomList(user));
    }
}
