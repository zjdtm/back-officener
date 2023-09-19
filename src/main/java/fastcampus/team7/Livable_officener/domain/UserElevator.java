package fastcampus.team7.Livable_officener.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class UserElevator extends BaseEntity{

    private Long ElevatorId;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
