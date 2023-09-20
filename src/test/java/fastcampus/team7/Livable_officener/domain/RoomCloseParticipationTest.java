package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.NotActiveRoomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomCloseParticipationTest {

    @DisplayName("활성상태가 아니면 예외 발생")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, names = {"CLOSED", "TERMINATED"})
    void whenInactive_thenThrowsException(RoomStatus status) {
        // given
        Room room = Room.builder()
                .status(status)
                .build();

        // when, then
        assertThatThrownBy(room::closeParticipation)
                .isInstanceOf(NotActiveRoomException.class);
    }

    @DisplayName("활성상태면 참여마감 상태로 변경")
    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, names = "ACTIVE")
    void whenActive_thenModifyStatusToClosed(RoomStatus status) {
        // given
        Room room = Room.builder()
                .status(status)
                .build();

        // when
        room.closeParticipation();

        // then
        assertThat(room.getStatus()).isSameAs(RoomStatus.CLOSED);
    }
}
