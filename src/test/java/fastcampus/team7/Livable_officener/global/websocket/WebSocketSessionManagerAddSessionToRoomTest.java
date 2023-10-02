package fastcampus.team7.Livable_officener.global.websocket;

import fastcampus.team7.Livable_officener.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketSession;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
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

    @DisplayName("같은 방에 같은 유저 중복되는 세션 존재하면 예외")
    @Test
    void whenExistentDuplicateSession_thenThrowsException() {
        // given
        final Long roomId = 1L;
        final Long userId = 1L;
        User user = User.builder().id(userId).build();
        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getPrincipal()).willReturn(user);
        sut.addSessionToRoom(roomId, session);

        User newUser = User.builder().id(userId).build();
        WebSocketSession newSession = mock(WebSocketSession.class);
        given(newSession.getPrincipal()).willReturn(newUser);

        // when, then
        assertThatThrownBy(() -> sut.addSessionToRoom(roomId, newSession))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("이미 방이 존재하고 중복되지 않는 세션이면 패스")
    @Test
    void whenExistentRoomAndNotDuplicateSession_thenPass() {
        // given
        Long roomId = 1L;
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        WebSocketSession session = mock(WebSocketSession.class);
        given(session.getPrincipal()).willReturn(user);
        sut.addSessionToRoom(roomId, session);

        Long newUserId = 2L;
        User newUser = User.builder().id(newUserId).build();
        WebSocketSession newSession = mock(WebSocketSession.class);
        given(newSession.getPrincipal()).willReturn(newUser);

        // when
        sut.addSessionToRoom(roomId, newSession);

        // then
        assertThatNoException().as("패스");
    }
}
