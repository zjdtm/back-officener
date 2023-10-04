package fastcampus.team7.Livable_officener.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.team7.Livable_officener.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.WebSocketSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class WebSocketSessionManagerNonexistentTest {

    private final WebSocketSessionManager sut = new WebSocketSessionManager(new ObjectMapper());

    @DisplayName("특정 방에 주어진 유저의 세션이 존재하지 않으면 true")
    @Test
    void givenNonexistent_thenTrue() {
        // given
        final Long roomId = 1L;
        final int numSessions = 5;
        for (int i = 1; i <= numSessions; ++i) {
            User user = User.builder().id((long) i).build();
            WebSocketSession session = mock(WebSocketSession.class);
            given(session.getPrincipal()).willReturn(user);
            sut.addSessionToRoom(roomId, session);
        }

        // when
        Long userId = numSessions + 1L;
        User user = User.builder().id(userId).build();
        boolean nonexistent = sut.nonexistent(roomId, user);

        // then
        assertThat(nonexistent).isTrue();
    }

    @DisplayName("특정 방에 주어진 유저의 세션이 존재하면 false")
    @Test
    void givenExistent_thenFalse() {
        // given
        final Long roomId = 1L;
        final int numSessions = 6;
        for (int i = 1; i <= numSessions; ++i) {
            User user = User.builder().id((long) i).build();
            WebSocketSession session = mock(WebSocketSession.class);
            given(session.getPrincipal()).willReturn(user);
            sut.addSessionToRoom(roomId, session);
        }

        // when
        Long userId = (long) (Math.random() * 6) + 1L;
        User user = User.builder().id(userId).build();
        boolean nonexistent = sut.nonexistent(roomId, user);

        // then
        assertThat(nonexistent).isFalse();
    }
}