package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.User;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequestDTO {

    private String email;

    private String password;

    private String buildingName;

    private String companyName;

    private String name;

    private String phoneNumber;

    private boolean agree;

    public User toEntity(Building building) {
        return User.builder()
                .email(email)
                .password(password)
                .building(building)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();
    }

}
