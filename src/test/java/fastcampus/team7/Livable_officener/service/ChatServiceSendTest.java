package fastcampus.team7.Livable_officener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fastcampus.team7.Livable_officener.dto.chat.SendPayloadDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.util.LocalDateTimeDeserializer;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ChatServiceSendTest {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
//        JavaTimeModule module = new JavaTimeModule();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(module);
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
        System.out.println("payload = " + payload);
//        LocalDateTime parsedSendTimeLocal = LocalDateTime.parse(sendTimeLocal);
//        System.out.println("parsedSendTimeLocal = " + parsedSendTimeLocal);

        // when
        SendPayloadDTO payloadDto = mapper.readValue(payload, SendPayloadDTO.class);

        // then
        assertThat(payloadDto.getMessageType()).isSameAs(messageType);
        assertThat(payloadDto.getContent()).isEqualTo(content);
//        assertThat(payloadDto.getSendTime()).isEqualTo(ZonedDateTime.parse(sendTimeLocal).toOffsetDateTime().toString());
        assertThat(payloadDto.getSendTime()).isEqualTo(LocalDateTime.parse(sendTimeLocal));

        String payloadWritten = mapper.writeValueAsString(payloadDto);
        System.out.println("payloadWritten = " + payloadWritten);
    }
}
