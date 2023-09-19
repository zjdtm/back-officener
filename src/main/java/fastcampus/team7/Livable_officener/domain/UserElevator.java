package fastcampus.team7.Livable_officener.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;


@Entity
@Getter
@Setter
public class UserElevator extends BaseEntity{

    @Column(nullable = false)
    private Long ElevatorId;

    @Column(nullable = false)
    private Long userId;
}
