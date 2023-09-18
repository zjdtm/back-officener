package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
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
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime kickedAt;

    @Column
    private LocalDateTime transferredAt;

    public void completeTransfer() {
        transferredAt = LocalDateTime.now();
    }
}
