package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.global.constant.Direction;
import fastcampus.team7.Livable_officener.global.constant.ElevatorStatus;
import lombok.Data;

import javax.persistence.*;

@Data
public class ElevatorDTO {

    private Long id;

    private Long floor;

    private Direction direction;

    private ElevatorStatus status;

}
