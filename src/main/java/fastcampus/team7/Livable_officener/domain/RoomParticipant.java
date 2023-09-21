package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.Role;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class RoomParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column
    private LocalDateTime kickedAt;

    @Column
    private LocalDateTime remittedAt;

    @Column
    private LocalDateTime receivedAt;

    public void completeRemit() {
        remittedAt = LocalDateTime.now();
    }

    public void completeReceive() {
        receivedAt = LocalDateTime.now();
    }
}
