package fastcampus.team7.Livable_officener.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BuildingDTO {

    private Long id;

    private String buildingName;

    private String buildingAddress;

}
