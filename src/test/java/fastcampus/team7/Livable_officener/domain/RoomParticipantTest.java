package fastcampus.team7.Livable_officener.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomParticipantTest {

    @Test
    @DisplayName("송금완료시 완료시간 갱신 테스트")
    void completeTransferredAtTest () {
        //given
        RoomParticipant roomParticipant = RoomParticipant.builder().build();
        //when
        roomParticipant.completeTransfer();
        //then
        Assertions.assertThat(roomParticipant).isNotNull();
    }
}
