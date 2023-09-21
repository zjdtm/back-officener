package fastcampus.team7.Livable_officener.global.websocket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

class WebSocketSessionManagerAddSessionToRoomTest {

    private final WebSocketSessionManager sut = new WebSocketSessionManager();

    @DisplayName("roomId에 해당하는 세션 Collection 없어도 패스")
    @Test
    void whenNoCollectionOfSessions_thenPass() {
        // given
        Long roomId = 1L;
        WebSocketSession session = mock(WebSocketSession.class);

        // when
        sut.addSessionToRoom(roomId, session);

        // then
        assertThatNoException().as("패스");
    }
}