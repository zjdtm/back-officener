package fastcampus.team7.Livable_officener.global.websocket;

import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebSocketSessionManagerSendTest {

    private final WebSocketSessionManager sut = new WebSocketSessionManager();

    @DisplayName("roomId에 해당하는 세션 Collection 없으면 예외")
    @Test
    void whenNoCollectionOfSessions_thenThrowsNotFoundRoomException() {
        // given
        // 빈 객체이므로 어떤 roomId 든지 해당하는 세션 Collection 없음

        // when, then
        assertThatThrownBy(() -> sut.send(1L, new TextMessage("payload")))
                .isInstanceOf(NotFoundRoomException.class);
    }
}
