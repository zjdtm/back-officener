package fastcampus.team7.Livable_officener.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class WebSocketSessionManagerSendToAllTest {

    private final WebSocketSessionManager sut = new WebSocketSessionManager(new ObjectMapper());

    @DisplayName("roomId에 해당하는 세션 Collection 없으면 예외")
    @Test
    void whenNoCollectionOfSessions_thenThrowsNotFoundRoomException() {
        // given
        // 빈 객체이므로 어떤 roomId 든지 해당하는 세션 Collection 없음

        // when, then
        assertThatThrownBy(() -> sut.sendToAll(1L, new TextMessage("payload")))
                .isInstanceOf(NotFoundRoomException.class);
    }

    @DisplayName("송신 시 같은 방의 모든 세션에게 메시지 송신")
    @Test
    void success() throws IOException {
        // given
        Long roomId = 1L;
        TextMessage message = new TextMessage("payload");
        int numSessions = 3;
        Collection<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
        for (int i = 1; i <= numSessions; ++i) {
            User user = mock(User.class);
            WebSocketSession session = mock(WebSocketSession.class);
            given(session.getPrincipal()).willReturn(user);
            sut.addSessionToRoom(1L, session);
        }

        // when
        sut.sendToAll(roomId, message);

        // then
        sessions.forEach(session -> {
            try {
                verify(session, times(1)).sendMessage(eq(message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
