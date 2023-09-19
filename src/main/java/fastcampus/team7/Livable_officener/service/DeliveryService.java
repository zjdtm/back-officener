package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Bank;
import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.DeliveryRequestDTO;
import fastcampus.team7.Livable_officener.dto.RoomDetailDTO;
import fastcampus.team7.Livable_officener.dto.UpdateStoreDetailDTO;
import fastcampus.team7.Livable_officener.dto.DeliveryResponseDTO;
import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotParticipantException;
import fastcampus.team7.Livable_officener.repository.BankRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryParticipantRepository deliveryParticipantRepository;
    private final BankRepository bankRepository;

    @Transactional
    public void registerRoom(DeliveryRequestDTO.createDTO createDTO, User user) {
        FoodTag foodTag = FoodTag.valueOf(createDTO.getFoodTag().toUpperCase());
        BankName bankName = BankName.valueOf(createDTO.getBankName().toUpperCase());
        RoomStatus status = RoomStatus.ACTIVE;

        DeliveryRequestDTO.roomSaveDTO roomSaveDTO = DeliveryRequestDTO.roomSaveDTO.builder()
                .storeName(createDTO.getStoreName())
                .menuLink(createDTO.getMenuLink())
                .deliveryFee(createDTO.getDeliveryFee())
                .foodTag(foodTag)
                .bankName(bankName)
                .accountNumber(createDTO.getAccountNumber())
                .hostName(user.getName())
                .deadline(createDTO.getDeadline())
                .attendees(1L)
                .maxAttendees(createDTO.getMaxAttendees())
                .desc(createDTO.getDesc())
                .status(status)
                .build();

        Room savedRoom = deliveryRepository.save(roomSaveDTO.toEntity());
        Long roomId = savedRoom.getId();

        DeliveryRequestDTO.roomParticipantSaveDTO roomParticipantSaveDTO = DeliveryRequestDTO.roomParticipantSaveDTO.builder()
                .roomId(roomId)
                .userId(user.getId())
                .role(Role.HOST)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .kickedAt(null)
                .build();

        Room room = deliveryRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("올바른 roomId 아님"));
        deliveryParticipantRepository.save(roomParticipantSaveDTO.toEntity(room, user));
    }

    public RoomDetailDTO selectRoomDetail(Long id) {
        // TODO : 예외처리 로직 추가
        return deliveryRepository.findRoomById(id);
    }

    @Transactional
    public Map<String, List<Map<String, String>>> loadBankList() {
        List<Bank> bankList = bankRepository.findAll();

        List<Map<String, String>> responseData = bankList.stream()
                .map(bank -> {
                    Map<String, String> bankMap = new HashMap<>();
                    bankMap.put("bankName", bank.getName().getName());
                    return bankMap;
                })
                .collect(Collectors.toList());

        Map<String, List<Map<String, String>>> response = new HashMap<>();
        response.put("banks", responseData);

        return response;
    }

    @Transactional
    public void updateStoreDetail(Long roomId, UpdateStoreDetailDTO requestDTO, User user) {
        Room room = checkHost(roomId, user);

        room.updateStoreDetail(requestDTO);
    }

    @Transactional
    public void deleteDelivery(Long roomId, User user) {
        Room room = checkHost(roomId, user);

        deliveryParticipantRepository.deleteAllByRoomId(roomId);
        deliveryRepository.delete(room);
    }

    private Room checkHost(Long roomId, User user) {
        Room room = deliveryRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않는 방입니다."));

        Optional<RoomParticipant> roomParticipant = Optional.ofNullable(
                deliveryParticipantRepository.findRoomParticipant(roomId, user.getId())
                        .orElseThrow(UserIsNotParticipantException::new));

        if (roomParticipant.isPresent() && roomParticipant.get().getRole() == Role.GUEST) {
            throw new IllegalArgumentException("호스트가 아닌 사람은 수정할 수 없습니다.");
        }
        return room;
    }

    public DeliveryResponseDTO.PagedRoomListResponseDTO getRoomList(Pageable pageable) {
        Page<Room> rooms = deliveryRepository.findAll(pageable);

        DeliveryResponseDTO.PagedRoomListResponseDTO response = new DeliveryResponseDTO.PagedRoomListResponseDTO();
        response.setCurrentPage(rooms.getNumber());
        response.setTotalPage(rooms.getTotalPages());
        response.setTotalElements(rooms.getTotalElements());

        response.setRooms(rooms.stream()
                .map(this::convertToRoomListResponseDTO)
                .collect(Collectors.toList()));

        return response;

    }

    private DeliveryResponseDTO.RoomListResponseDTO convertToRoomListResponseDTO(Room room) {
        String foodTag = room.getTag().toString();
        String roomStatus = room.getStatus().toString();
        return DeliveryResponseDTO.RoomListResponseDTO.builder()
                .roomId(room.getId())
                .hostId(1L)
                .storeName(room.getStoreName())
                .menuLink(room.getMenuLink())
                .deliveryFee(room.getDeliveryFee())
                .tag(foodTag)
                .attendees(room.getAttendees())
                .maxAttendees(room.getMaxAttendees())
                .deadLine(room.getDeadline())
                .roomStatus(roomStatus)
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }

    @Transactional
    public DeliveryResponseDTO.PagedRoomListResponseDTO getFilteredRoomList(Pageable pageable, User user) {
        Page<Room> rooms = deliveryRepository.findRoomsByUserId(user.getId(), pageable);

        DeliveryResponseDTO.PagedRoomListResponseDTO response = new DeliveryResponseDTO.PagedRoomListResponseDTO();
        response.setCurrentPage(rooms.getNumber());
        response.setTotalPage(rooms.getTotalPages());
        response.setTotalElements(rooms.getTotalElements());

        response.setRooms(rooms.stream()
                .map(this::convertToRoomListResponseDTO)
                .collect(Collectors.toList()));

        return response;
    }
}
