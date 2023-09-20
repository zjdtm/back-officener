package fastcampus.team7.Livable_officener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fastcampus.team7.Livable_officener.domain.Chat;
import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.SendChatDTO;
import fastcampus.team7.Livable_officener.dto.chat.SendPayloadDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.exception.*;
import fastcampus.team7.Livable_officener.global.websocket.WebSocketSessionManager;
import fastcampus.team7.Livable_officener.repository.ChatRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatService {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private final ChatRepository chatRepository;
    private final DeliveryRepository roomRepository;
    private final DeliveryParticipantRepository roomParticipantRepository;
    private final WebSocketSessionManager webSocketSessionManager;

    @Transactional
    public void send(SendChatDTO sendChatDTO) throws IOException {
        Room room = sendChatDTO.getRoom();
        User sender = sendChatDTO.getSender();
        SendPayloadDTO payloadDto = getSendPayloadDTO(sender, sendChatDTO.getMessage());

        sendMessage(room, sender, payloadDto);
    }

    private SendPayloadDTO getSendPayloadDTO(User sender, TextMessage message) throws JsonProcessingException {
        Long senderId = sender.getId();
        String sendPayload = message.getPayload();

        SendPayloadDTO sendPayloadDTO = objectMapper.readValue(sendPayload, SendPayloadDTO.class);
        sendPayloadDTO.setSenderId(senderId);
        return sendPayloadDTO;
    }

    @Transactional
    public void closeParticipation(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);

        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsHost(roomParticipant.getRole(), "참여마감하기");

        room.closeParticipation();

        sendSystemMessage(room, user, ChatType.CLOSE_PARTICIPATION);
    }

    @Transactional
    public void completeRemit(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);

        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsGuest(roomParticipant.getRole(), "송금완료");
        isRemitCompleted(roomParticipant);
        roomParticipant.completeRemit();

        sendSystemMessage(room, user, ChatType.COMPLETE_REMITTANCE);
    }

    @Transactional
    public void completeDelivery(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);

        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsHost(roomParticipant.getRole(), "배달완료");

        room.completeDelivery();

        sendSystemMessage(room, user, ChatType.COMPLETE_DELIVERY);
    }

    @Transactional
    public void completeReceive(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsGuest(roomParticipant.getRole(), "수령완료");
        isReceiveCompleted(roomParticipant);
        roomParticipant.completeReceive();

        sendSystemMessage(room, user, ChatType.COMPLETE_RECEIPT);
    }

    @Transactional
    public void kickRequest(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(room.getId(), user.getId());
        validateIfRoomParticipantIsGuest(roomParticipant.getRole(), "나가기요청");

        sendSystemMessage(room, user, ChatType.REQUEST_EXIT);
    }

    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(NotFoundRoomException::new);
    }

    private RoomParticipant getRoomParticipant(Long roomId, Long userId) {
        return roomParticipantRepository.findRoomParticipant(roomId, userId)
                .orElseThrow(UserIsNotParticipantException::new);
    }

    private static void validateIfRoomParticipantIsGuest(Role role, String requestName) {
        if (role != Role.GUEST) {
            throw new UserIsNotGuestException(requestName);
        }
    }

    private static void validateIfRoomParticipantIsHost(Role role, String requestName) {
        if (role != Role.HOST) {
            throw new UserIsNotHostException(requestName);
        }
    }

    private static void isRemitCompleted(RoomParticipant roomParticipant) {
        if (roomParticipant.getRemittedAt() != null) {
            throw new AlreadyRemittedException();
        }
    }

    private static void isReceiveCompleted(RoomParticipant roomParticipant) {
        if (roomParticipant.getReceivedAt() != null) {
            throw new AlreadyReceivedException();
        }
    }

    private void sendSystemMessage(Room room, User sender, ChatType messageType) throws IOException {
        SendPayloadDTO payloadDto = createSystemMessagePayloadDTO(sender, messageType);

        sendMessage(room, sender, payloadDto);
    }

    private static SendPayloadDTO createSystemMessagePayloadDTO(User sender, ChatType messageType) {
        String content = getSystemMessageContent(sender, messageType);
        return createSystemMessagePayloadDTO(sender, messageType, content);
    }

    private static String getSystemMessageContent(User sender, ChatType messageType) {
        Object[] systemMessageArgs = getSystemMessageArgs(messageType, sender);
        return messageType.getSystemMessageContent(systemMessageArgs);
    }

    private static Object[] getSystemMessageArgs(ChatType messageType, User user) {
        Object[] systemMessageArgs;
        if (messageType == ChatType.COMPLETE_DELIVERY) {
            systemMessageArgs = null;
        } else {
            systemMessageArgs = new Object[]{user.getName()};
        }
        return systemMessageArgs;
    }

    private static SendPayloadDTO createSystemMessagePayloadDTO(User sender, ChatType messageType, String content) {
        SendPayloadDTO systemMessagePayloadDTO = new SendPayloadDTO(messageType, content, LocalDateTime.now());
        systemMessagePayloadDTO.setSenderId(sender.getId());
        return systemMessagePayloadDTO;
    }

    private void sendMessage(Room room, User sender, SendPayloadDTO payloadDto) throws IOException {
        TextMessage message = convertPayloadDtoToJsonTextMessage(payloadDto);

        webSocketSessionManager.send(room.getId(), message);
        chatRepository.save(Chat.from(room, sender, payloadDto));
    }

    private TextMessage convertPayloadDtoToJsonTextMessage(SendPayloadDTO payloadDTO) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(payloadDTO);
        return new TextMessage(payload);
    }
}
