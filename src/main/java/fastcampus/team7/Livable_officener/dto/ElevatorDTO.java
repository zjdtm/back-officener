package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.global.constant.Direction;
import fastcampus.team7.Livable_officener.global.constant.ElevatorStatus;
import lombok.Data;

import java.util.List;


@Data
public class ElevatorDTO {

    private Long id;
    private Long floor;
    private Direction direction;
    private ElevatorStatus status;

    @Data
    public static class UserElevatorDTO{
        private List<Long> selectedIds;
    }

}
