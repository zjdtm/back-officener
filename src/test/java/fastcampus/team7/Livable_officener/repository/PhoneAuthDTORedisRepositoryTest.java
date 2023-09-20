package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.dto.PhoneAuthDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PhoneAuthDTORedisRepositoryTest {

    @Autowired
    private PhoneAuthDTORedisRepository phoneAuthDTORedisRepository;

    @Test
    @DisplayName("핸드폰 번호 인증번호 Redis 에 저장 테스트")
    void givenPhoneNumber_whenSaveRedis_thenSaveSuccessTest() {

        // given
        final String phoneNumber = "01012345678";
        final String verifyCode = "999999";

        // when
        phoneAuthDTORedisRepository.save(PhoneAuthDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(verifyCode)
                .build());

        // then
        PhoneAuthDTO phoneAuthDTO = phoneAuthDTORedisRepository.findById(phoneNumber).get();
        assertThat(phoneAuthDTO.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(phoneAuthDTO.getVerifyCode()).isEqualTo(verifyCode);

    }

    @Test
    @DisplayName("핸드폰 번호 인증번호 Redis 에 업데이트 테스트")
    void givenPhoneNumber_whenSaveRedis_thenUpdatedSuccessTest() {

        // given
        final String phoneNumber = "01012345678";
        final String verifyCode = "999999";
        final String changeVerifyCode = "111111";

        PhoneAuthDTO savedPhoneAuthDTO = phoneAuthDTORedisRepository.save(PhoneAuthDTO.builder()
                .phoneNumber(phoneNumber)
                .verifyCode(verifyCode)
                .build());

        // when
        savedPhoneAuthDTO.changeVerifyCode("111111");

        // then
        assertThat(savedPhoneAuthDTO.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(savedPhoneAuthDTO.getVerifyCode()).isEqualTo(changeVerifyCode);

    }

}
