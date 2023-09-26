package fastcampus.team7.Livable_officener.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class LoginRequestDTO {

    @Email(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "유효하지 않은 이메일 주소입니다.")
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W_])[a-zA-Z\\d\\W_]{8,16}$",
            message = "비밀번호는 영문, 숫자, 특수기호 를 포함한 8 ~ 16자입니다.")
    @NotBlank(message = "패스워드를 입력해주세요")
    private String password;

}
