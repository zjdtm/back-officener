package fastcampus.team7.Livable_officener.global.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fastcampus.team7.Livable_officener.domain.Company;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.SendPayloadDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.constant.Role;
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

class WebSocketSessionManagerSendEnterMessageToAllTest {

    private final WebSocketSessionManager sut;

    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        sut = new WebSocketSessionManager(mapper);
    }

    @DisplayName("송신 시 각 송신자와 수신자가 같은지 여부 확인")
    @Test
    void success() throws IOException {
        // given
        final Long roomId = 1L;
        final int numSessions = 6;
        final Collection<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());

        User enteringUser = null;
        for (int i = 1; i <= numSessions; ++i) {
            User user = mock(User.class);
            String name = "테스트" + i;
            given(user.toString()).willReturn(name);
            WebSocketSession session = mock(WebSocketSession.class);
            given(session.getPrincipal()).willReturn(user);
            sut.addSessionToRoom(1L, session);
            sessions.add(session);
            if (i == numSessions) {
                given(user.getName()).willReturn(name);
                Company company = Company.builder().name("테스트회사").build();
                given(user.getCompany()).willReturn(company);
                enteringUser = user;
            }
        }

        assertThat(enteringUser).isNotNull();

        String content = ChatType.ENTER.getSystemMessageContent(enteringUser);
        RoomParticipant newParticipant = RoomParticipant.builder()
                .user(enteringUser)
                .role(Role.GUEST)
                .build();

        final SendPayloadDTO.Enter enterPayloadDto = new SendPayloadDTO.Enter(content, newParticipant);

        // when
        sut.sendEnterMessageToAll(roomId, enteringUser, enterPayloadDto);

        // then
        final String name = enteringUser.getName();
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        for (WebSocketSession session : sessions) {
            verify(session, times(1)).sendMessage(messageCaptor.capture());
            TextMessage message = messageCaptor.getValue();
            User receiver = (User) session.getPrincipal();
            assertThat(message.getPayload()).contains("\"content\":\"" + name);
            assertThat(message.getPayload()).contains("\"name\":\"" + name);
            assertThat(message.getPayload()).contains("\"amI\":" + enteringUser.equals(receiver));
        }
    }
}
