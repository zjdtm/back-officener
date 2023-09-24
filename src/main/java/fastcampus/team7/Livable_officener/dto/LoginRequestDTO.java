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
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,15}$",
            message = "비밀번호는 최소 8자 이상 15자 이하, 대문자, 소문자, 숫자, 특수 문자($@$!%*?&)를 포함해야 합니다.")
    @NotBlank(message = "패스워드를 입력해주세요")
    private String password;

}
