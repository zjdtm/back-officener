package fastcampus.team7.Livable_officener.dto.delivery;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RoomParticipantSaveDTO {
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
