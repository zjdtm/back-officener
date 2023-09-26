package fastcampus.team7.Livable_officener.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginResponseDTO {

    private Long id;

    private String email;

    private String name;

    private String phoneNumber;

    private BuildingDTO building;

    private CompanyDTO company;

    private String token;
}
