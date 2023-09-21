package fastcampus.team7.Livable_officener.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequestDTO {

    private String email;

    private String password;

}
