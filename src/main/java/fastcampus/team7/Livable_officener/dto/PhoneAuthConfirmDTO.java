package fastcampus.team7.Livable_officener.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PhoneAuthConfirmDTO {

    private String phoneNumber;

    private String verifyCode;
}
