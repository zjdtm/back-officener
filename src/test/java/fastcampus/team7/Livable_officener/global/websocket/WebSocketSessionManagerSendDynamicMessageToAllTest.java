package fastcampus.team7.Livable_officener.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.SendPayloadDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class WebSocketSessionManagerSendDynamicMessageToAllTest {

    private final WebSocketSessionManager sut;

    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        sut = new WebSocketSessionManager(mapper);
    }

    @DisplayName("송신 시 각 참여자의 이름에 맞게 content 생성")
    @Test
    void success() throws IOException {
        // given
        final Long roomId = 1L;
        final int numSessions = 6;
        final Collection<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 1; i <= numSessions; ++i) {
            User user = mock(User.class);
            given(user.toString()).willReturn("테스트" + i);
            WebSocketSession session = mock(WebSocketSession.class);
            given(session.getPrincipal()).willReturn(user);
            sut.addSessionToRoom(1L, session);
            sessions.add(session);
        }

        final SendPayloadDTO payloadDTO = new SendPayloadDTO(ChatType.CLOSE_PARTICIPATION, 1L);

        // when
        sut.sendDynamicMessageToAll(roomId, payloadDTO);

        // then
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        for (WebSocketSession session : sessions) {
            verify(session, times(1)).sendMessage(messageCaptor.capture());
            TextMessage message = messageCaptor.getValue();
            User participant = (User) session.getPrincipal();
            assertThat(participant).isNotNull();
            assertThat(message.getPayload()).contains(participant.toString());
//            System.out.println("participant = " + participant);
        }
    }
}