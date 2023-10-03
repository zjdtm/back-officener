package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.*;
import fastcampus.team7.Livable_officener.dto.*;
import fastcampus.team7.Livable_officener.dto.delivery.RoomDetailDTO;
import fastcampus.team7.Livable_officener.dto.delivery.UpdateStoreDetailDTO;
import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
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

import static fastcampus.team7.Livable_officener.dto.delivery.ChatRoomListResponseDTO.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryParticipantRepository deliveryParticipantRepository;
    private final BankRepository bankRepository;

    @Transactional
    public void registerRoom(DeliveryRequestDTO.createDTO createDTO, User user) {
        FoodTag foodTag = FoodTag.fromFoodNameToCode(createDTO.getFoodTag().toUpperCase());
        BankName bankName = BankName.fromBankNameToCode(createDTO.getBankName().toUpperCase());
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
    public DeliveryResponseDTO.BankListResponseDTO loadBankList() {
        List<Bank> bankList = bankRepository.findAll();
        return new DeliveryResponseDTO.BankListResponseDTO(bankList);
    }

    @Transactional
    public void updateStoreDetail(Long roomId, UpdateStoreDetailDTO requestDTO, User user) {
        Room room = checkHost(roomId, user);

        room.updateStoreDetail(requestDTO);
    }

    @Transactional
    public void deleteDelivery(Long roomId, User user) {
        Room room = checkHost(roomId, user);

        room.updateRoomStatus();
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

    private DeliveryResponseDTO.RoomListResponseDTO convertToRoomListResponseDTO(Room room) {
        String foodTag = room.getTag().toString();
        String roomStatus = room.getStatus().toString();
        Long hostId = findHostIdByRoom(room.getId());
        return DeliveryResponseDTO.RoomListResponseDTO.builder()
                .roomId(room.getId())
                .hostId(hostId)
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

    private Long findHostIdByRoom(Long roomId) {
        return deliveryParticipantRepository.findUserIdByRoomIdAndRole(roomId, Role.HOST)
                .orElseThrow(() -> new NotFoundRoomException("해당 roomId로 검색되는 room이 없거나, room이 존재하지 않습니다."));
    }


    public MyChatListResponseDTO getChatRoomList(User user) {
        List<ChatRoomListDTO> chatRoomListDTO = deliveryRepository.findChatRoomList(user.getId());

        MyChatListResponseDTO myChatListResponseDTO = new MyChatListResponseDTO();
        myChatListResponseDTO.listOf(chatRoomListDTO);

        return myChatListResponseDTO;
    }

    @Transactional
    public void joinDeliveryRoom (Long roomId, User user){
        Room room = deliveryRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundRoomException("유효하지 않은 방입니다."));

        if (room.getAttendees() >= room.getMaxAttendees()) {
            throw new IllegalArgumentException("함께배달 방의 현재 인원이 최대 인원보다 많거나 같습니다. 만원입니다.");
        }

        if (user.getId() == findHostIdByRoom(roomId)) {
            throw new IllegalArgumentException("이미 해당 함께배달 방에 참여중이며, 호스트 입니다.");
        }

        room.setAttendees(room.getAttendees() + 1);
        Room updatedRoom = deliveryRepository.save(room);

        RoomParticipant roomParticipant = RoomParticipant.builder()
                .room(updatedRoom)
                .user(user)
                .role(Role.GUEST)
                .kickedAt(null)
                .build();

        deliveryParticipantRepository.save(roomParticipant);
    }
}
