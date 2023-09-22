package fastcampus.team7.Livable_officener.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BuildingWithCompaniesDTO {

    private Long id;
    private String buildingName;
    private String buildingAddress;
    private List<CompanyDTO> offices;

}
