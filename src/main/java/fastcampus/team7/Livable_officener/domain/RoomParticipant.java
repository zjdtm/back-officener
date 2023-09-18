package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.Role;
import lombok.Getter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Entity
public class RoomParticipant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    private Timestamp kickedAt;

}
