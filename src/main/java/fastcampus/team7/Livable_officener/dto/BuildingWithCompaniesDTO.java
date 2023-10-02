package fastcampus.team7.Livable_officener.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class BuildingWithCompaniesDTO {

    @JsonProperty("buildings")
    private List<BuildingWithCompaniesResponseDTO> buildings;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class BuildingWithCompaniesResponseDTO {
        private Long id;
        private String buildingName;
        private String buildingAddress;
        private List<CompanyResponseDTO> offices;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        @AllArgsConstructor
        public static class CompanyResponseDTO {
            private Long id;
            private String officeName;
            private String officeNum;
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class BuildingResponseDTO {

        private Long id;

        private String buildingName;

        private String buildingAddress;

    }


}
