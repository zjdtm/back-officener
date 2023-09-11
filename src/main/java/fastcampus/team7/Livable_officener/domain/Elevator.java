package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.Direction;
import fastcampus.team7.Livable_officener.global.constant.ElevatorStatus;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Elevator extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Building building;

    @Column(nullable = false)
    private Long floor;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Direction direction;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ElevatorStatus status;

}
