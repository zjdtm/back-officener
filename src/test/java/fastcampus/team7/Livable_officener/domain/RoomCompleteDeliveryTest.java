package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.exception.AlreadyDeliveredException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoomCompleteDeliveryTest {

    @DisplayName("이미 배달완료이면 예외발생")
    @Test
    void whenDeliveryAlreadyCompleted_thenThrowsException() {
        // given
        Room room = Room.builder()
                .deliveredAt(LocalDateTime.now())
                .build();

        // when, then
        assertThatThrownBy(room::completeDelivery)
                        .isInstanceOf(AlreadyDeliveredException.class);
    }

}