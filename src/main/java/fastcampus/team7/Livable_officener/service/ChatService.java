package fastcampus.team7.Livable_officener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fastcampus.team7.Livable_officener.domain.Chat;
import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.*;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.*;
import fastcampus.team7.Livable_officener.global.util.LocalDateTimeDeserializer;
import fastcampus.team7.Livable_officener.global.websocket.WebSocketSessionManager;
import fastcampus.team7.Livable_officener.repository.ChatRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private final ChatRepository chatRepository;
    private final DeliveryRepository roomRepository;
    private final DeliveryParticipantRepository roomParticipantRepository;
    private final WebSocketSessionManager webSocketSessionManager;

    @PostConstruct
    public void setup() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        objectMapper.registerModule(module);
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }

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

    @Transactional
    public void exitChatRoom(Long roomId, User user) {
        getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());

        validateIfRoomParticipantIsHost(roomParticipant.getRole(), "나가기");
        validateAllParticipantsCompletedRemitAndReceive(roomId);

        roomRepository.deleteById(roomId);
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

    private void validateAllParticipantsCompletedRemitAndReceive(Long roomId) {
        List<RoomParticipant> participants = roomParticipantRepository.findAllByRoomId(roomId);

        for (RoomParticipant participant : participants) {
            if (participant.getRemittedAt() == null) {
                throw new RemitNotCompletedException();
            }
            if (participant.getReceivedAt() == null) {
                throw new ReceiveNotCompletedException();
            }
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
        return new SendPayloadDTO(messageType, content, LocalDateTime.now(), sender.getId());
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

    private void sendMessage(Room room, User sender, SendPayloadDTO payloadDto) throws IOException {
        TextMessage message = convertPayloadDtoToJsonTextMessage(payloadDto);

        webSocketSessionManager.send(room.getId(), message);
        chatRepository.save(Chat.from(room, sender, payloadDto));

        // 함께배달 참여자 중 웹소켓세션이 연결되어있지 않은(=채팅 페이지를 벗어난) 참여자들의 unreadCount 증가
        roomParticipantRepository.findAllByRoomId(room.getId()).stream()
                .filter(participant -> webSocketSessionManager.nonexistent(room.getId(), participant.getUser()))
                .forEach(RoomParticipant::incrementUnreadCount);
    }

    private TextMessage convertPayloadDtoToJsonTextMessage(SendPayloadDTO payloadDTO) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(payloadDTO);
        return new TextMessage(payload);
    }

    @Transactional
    public ChatroomInfoDTO getChatroomInfo(Long roomId, User user) {
        Room room = getRoom(roomId);
        validateIfRoomIsActive(room);

        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        roomParticipant.resetUnreadCount();

        return createChatRoomInfoDTO(roomId, user.getId());
    }

    private static void validateIfRoomIsActive(Room room) {
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new NotActiveRoomException();
        }
    }

    private ChatroomInfoDTO createChatRoomInfoDTO(Long roomId, Long userId) {
        List<GetMessageDTO> messages = chatRepository.findByRoomIdOrderByCreatedAtDesc(roomId).stream()
                .map(GetMessageDTO::from)
                .collect(Collectors.toList());
        List<GetParticipantDTO> members = roomParticipantRepository.findAllByRoomId(roomId).stream()
                .map(participant -> GetParticipantDTO.from(userId, participant))
                .collect(Collectors.toList());
        return new ChatroomInfoDTO(messages, members);
    }
}
