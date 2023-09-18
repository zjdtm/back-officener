package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.BankName;
import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
public class DeliveryRequestDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class createDTO {
        private String storeName;
        private String menuLink;
        private Long deliveryFee;
        private String foodTag;
        private String bankName;
        private String accountNumber;
        private LocalDateTime deadline;
        private Long maxAttendees;
        private String desc;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class roomSaveDTO {
        private String storeName;
        private String menuLink;
        private Long deliveryFee;
        private FoodTag foodTag;
        private BankName bankName;
        private String accountNumber;
        private String hostName;
        private LocalDateTime deadline;
        private Long attendees;
        private Long maxAttendees;
        private String desc;
        private RoomStatus status;

        public Room toEntity() {
            return Room.builder()
                    .storeName(storeName)
                    .menuLink(menuLink)
                    .deliveryFee(deliveryFee)
                    .tag(foodTag)
                    .bankName(bankName)
                    .accountNumber(accountNumber)
                    .hostName(hostName)
                    .deadline(deadline)
                    .attendees(attendees)
                    .maxAttendees(maxAttendees)
                    .description(desc)
                    .status(status)
                    .build();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class roomParticipantSaveDTO {
        private Long roomId;
        private Long userId;
        private Role role;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime kickedAt;

        public RoomParticipant toEntity(Room room, User user) {
            return RoomParticipant.builder()
                    .room(room)
                    .user(user)
                    .role(role)
                    .kickedAt(kickedAt)
                    .build();
        }

    }
}
