package fastcampus.team7.Livable_officener.dto.signup;

import lombok.*;

public class PhoneAuthDTO {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class PhoneAuthRequestDTO {

        private String name;

        private String phoneNumber;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class PhoneAuthResponseDTO {

        private String verifyCode;

    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class PhoneAuthConfirmDTO {

        private String phoneNumber;

        private String verifyCode;
    }

}
