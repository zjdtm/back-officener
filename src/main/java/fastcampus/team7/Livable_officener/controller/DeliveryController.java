package fastcampus.team7.Livable_officener.controller;


import fastcampus.team7.Livable_officener.dto.DeliveryResponseDTO;
import fastcampus.team7.Livable_officener.dto.RoomDetailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.DeliveryRequestDTO;
import fastcampus.team7.Livable_officener.dto.RoomDetailDTO;
import fastcampus.team7.Livable_officener.dto.UpdateStoreDetailDTO;
import fastcampus.team7.Livable_officener.global.util.APIDataResponse;
import fastcampus.team7.Livable_officener.repository.BankRepository;
import fastcampus.team7.Livable_officener.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping("/{id}")
    public ResponseEntity<?> findRoomDetail(@PathVariable Long id) {
        final RoomDetailDTO roomDetail = deliveryService.selectRoomDetail(id);
        return ResponseEntity.ok(roomDetail);
    }

    @GetMapping("/bankList")
    public ResponseEntity<APIDataResponse<Map<String, List<Map<String, String>>>>> bankList() {
        Map<String, List<Map<String, String>>> response = deliveryService.loadBankList();
        return APIDataResponse.of(HttpStatus.OK, "성공", response);
    }

    @PostMapping("/create")
    public ResponseEntity<APIDataResponse<String>> create(
            @RequestBody DeliveryRequestDTO.createDTO createDTO,
            @AuthenticationPrincipal User user) {

        deliveryService.registerRoom(createDTO, user);

        return APIDataResponse.of(HttpStatus.CREATED, "성공", "API 성공");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyStoreDetail(
            @PathVariable Long id,
            @RequestBody UpdateStoreDetailDTO requestDTO,
            @AuthenticationPrincipal User user) {
        deliveryService.updateStoreDetail(id, requestDTO, user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteDelivery(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        deliveryService.deleteDelivery(id, user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<APIDataResponse<DeliveryResponseDTO.PagedRoomListResponseDTO>> list(@RequestParam(defaultValue = "0") int page,
                                                                                              @RequestParam(defaultValue = "10") int size) {
        DeliveryResponseDTO.PagedRoomListResponseDTO response = deliveryService.getRoomList(PageRequest.of(page, size));
        return APIDataResponse.of(HttpStatus.CREATED, "성공", response);
    }

    @GetMapping("joinedRoom")
    public ResponseEntity<APIDataResponse<DeliveryResponseDTO.PagedRoomListResponseDTO>> joinedRoom(@RequestParam(defaultValue = "0") int page,
                                                                                                    @RequestParam(defaultValue = "10") int size,
                                                                                                    @AuthenticationPrincipal User user) {

        DeliveryResponseDTO.PagedRoomListResponseDTO response = deliveryService.getFilteredRoomList(PageRequest.of(page, size), user);
        return APIDataResponse.of(HttpStatus.CREATED, "성공", response);
    }
}
