package fastcampus.team7.Livable_officener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fastcampus.team7.Livable_officener.domain.Chat;
import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.SendChatDTO;
import fastcampus.team7.Livable_officener.dto.chat.SendPayloadDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.util.LocalDateTimeDeserializer;
import fastcampus.team7.Livable_officener.global.websocket.WebSocketSessionManager;
import fastcampus.team7.Livable_officener.repository.ChatRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceSendTest {

    private final static ObjectMapper mapper;

    private static ChatService sut;
    @Mock
    private static ChatRepository chatRepository;
    @Mock
    private static DeliveryRepository roomRepository;
    @Mock
    private static DeliveryParticipantRepository roomParticipantRepository;
    @Mock
    private static WebSocketSessionManager webSocketSessionManager;

    static {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(module);
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @BeforeEach
    void beforeEach() {
        sut = new ChatService(mapper, chatRepository, null, null,
                roomRepository, roomParticipantRepository,
                webSocketSessionManager);
    }

    @DisplayName("mapper UTC -> Local")
    @Test
    void mapperShouldMapUTCToLocal() throws JsonProcessingException {
        // given
        Map<String, Object> map = new HashMap<>();
        final ChatType messageType = ChatType.TALK;
        final String content = "테스트 메시지";
        final String sendTime = "2023-09-22T10:08:24.395Z";
        final String sendTimeLocal = "2023-09-22T19:08:24.395";
        map.put("messageType", messageType.name());
        map.put("content", content);
        map.put("sendTime", sendTime);
        String payload = new JSONObject(map).toString();
//        System.out.println("payload = " + payload);
//        LocalDateTime parsedSendTimeLocal = LocalDateTime.parse(sendTimeLocal);
//        System.out.println("parsedSendTimeLocal = " + parsedSendTimeLocal);

        // when
        SendPayloadDTO payloadDto = mapper.readValue(payload, SendPayloadDTO.class);

        // then
        assertThat(payloadDto.getMessageType()).isSameAs(messageType);
        assertThat(payloadDto.getContent()).isEqualTo(content);
//        assertThat(payloadDto.getSendTime()).isEqualTo(ZonedDateTime.parse(sendTimeLocal).toOffsetDateTime().toString());
        assertThat(payloadDto.getSendTime()).isEqualTo(LocalDateTime.parse(sendTimeLocal));

//        String payloadWritten = mapper.writeValueAsString(payloadDto);
//        System.out.println("payloadWritten = " + payloadWritten);
    }

    @DisplayName("송신 및 저장되는 Chat에 senderId 포함")
    @Test
    void givenMessageWithoutSender_thenChatHasSender() throws IOException {
        // given
        Long roomId = 1L;
        Room room = mock(Room.class);
        given(room.getId()).willReturn(roomId);
        User sender = User.builder()
                .id(1L)
                .name("정준희")
                .email("joonhee@example.com")
                .build();
        Map<String, Object> map = new HashMap<>();
        final ChatType messageType = ChatType.TALK;
        final String content = "테스트 메시지";
        final String sendTime = "2023-09-22T10:08:24.395Z";
        final String sendTimeLocal = "2023-09-22T19:08:24.395";
        map.put("messageType", messageType.name());
        map.put("content", content);
        map.put("sendTime", sendTime);
        String payload = new JSONObject(map).toString();
        TextMessage message = new TextMessage(payload);
        SendChatDTO sendChatDTO = new SendChatDTO(room, sender, message);

        // when
        sut.send(sendChatDTO);

        // then
        ArgumentCaptor<Chat> chatCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository, times(1)).save(chatCaptor.capture());
        Chat chat = chatCaptor.getValue();
        assertThat(chat.getType()).isEqualTo(messageType);
        assertThat(chat.getContent()).isEqualTo(content);
        assertThat(chat.getCreatedAt()).isEqualTo(sendTimeLocal);
        assertThat(chat.getSender()).isEqualTo(sender);
    }

    @DisplayName("웹소켓 세션이 존재하지 않는 참여자들의 읽지 않은 메시지 수 증가")
    @Test
    void givenSomeWithoutWebSocketSession_thenTheirUnreadCountsIncrement() throws IOException {
        // given
        final Long roomId = 1L;
        final int numParticipants = 6;
        final int numSessions = 3;
        List<RoomParticipant> participants = new ArrayList<>(numParticipants);
        for (int i = 1; i <= numParticipants; ++i) {
            User user = User.builder()
                    .id((long) i)
                    .build();
            RoomParticipant participant = mock(RoomParticipant.class);
            given(participant.getUser())
                    .willReturn(user);
            participants.add(participant);
            given(webSocketSessionManager.nonexistent(roomId, user))
                    .willReturn(i <= numSessions);
        }

        given(roomParticipantRepository.findAllByRoomId(roomId))
                .willReturn(participants);

        // when
        Room room = mock(Room.class);
        given(room.getId())
                .willReturn(roomId);

        final String payload = "{\"messageType\":\"TALK\",\"content\":\"테스트 메시지\",\"sendTime\":\"2023-09-22T10:08:24.395Z\"}";
        final TextMessage message = new TextMessage(payload);
        SendChatDTO dto = new SendChatDTO(room, participants.get(0).getUser(), message);
        sut.send(dto);

        // then
        for (int i = 1; i <= numParticipants; ++i) {
            int callCount = (i <= numSessions) ? 1 : 0;
            verify(participants.get(i - 1), times(callCount)).incrementUnreadCount();
        }
    }

}
