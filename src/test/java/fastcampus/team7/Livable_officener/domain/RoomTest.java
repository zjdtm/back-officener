package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.NotActiveRoomException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomTest {

    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, names = {"CLOSED", "TERMINATED"})
    void 참여마감시_활성상태가아니면_예외발생(RoomStatus status) {
        // given
        Room room = Room.builder()
                .status(status)
                .build();

        // when, then
        assertThatThrownBy(room::closeParticipation)
                .isInstanceOf(NotActiveRoomException.class);
    }
}