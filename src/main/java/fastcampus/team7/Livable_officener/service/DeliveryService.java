package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.DeliveryRequestDTO;
import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.repository.BankRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import fastcampus.team7.Livable_officener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryParticipantRepository deliveryParticipantRepository;
    private final BankRepository bankRepository;
    private final UserRepository userRepository;

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
                .hostName("testUser")
                .deadline(createDTO.getDeadline())
                .attendees(1L)
                .maxAttendees(createDTO.getMaxAttendees())
                .desc(createDTO.getDesc())
                .status(status)
                .build();

        Room savedRoom = deliveryRepository.save(roomSaveDTO.toEntity());
        log.info("roomSave 통과");
        Long roomId = savedRoom.getId();

        DeliveryRequestDTO.roomParticipantSaveDTO roomParticipantSaveDTO = DeliveryRequestDTO.roomParticipantSaveDTO.builder()
                .roomId(roomId)
                .userId(1L)
                .role(Role.HOST)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .kickedAt(null)
                .build();

        Room room = deliveryRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("올바른 roomId 아님"));
        deliveryParticipantRepository.save(roomParticipantSaveDTO.toEntity(room, user));

    }
}
