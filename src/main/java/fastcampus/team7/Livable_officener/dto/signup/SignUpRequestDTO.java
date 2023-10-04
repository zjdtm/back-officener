package fastcampus.team7.Livable_officener.dto.signup;

import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.domain.User;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequestDTO {

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

    @NotBlank(message = "건물 명을 입력해주세요")
    private String buildingName;

    @NotBlank(message = "회사 명을 입력해주세요")
    private String officeName;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @Pattern(
            regexp = "^\\d{3}-?\\d{3,4}-?\\d{4}$",
            message = "핸드폰 번호는 숫자만 입력해주세요")
    private String phoneNumber;

    private boolean agree;

    public User toEntity(Building building, Company company, String encodingPassword) {
        return User.builder()
                .email(email)
                .password(encodingPassword)
                .building(building)
                .company(company)
                .name(name)
                .phoneNumber(phoneNumber)
                .build();
    }

}
