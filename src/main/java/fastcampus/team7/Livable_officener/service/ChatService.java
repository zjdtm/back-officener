package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Chat;
import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.SendChatDTO;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.constant.SystemMessage;
import fastcampus.team7.Livable_officener.global.exception.*;
import fastcampus.team7.Livable_officener.global.websocket.WebSocketSessionManager;
import fastcampus.team7.Livable_officener.repository.ChatRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final DeliveryRepository roomRepository;
    private final DeliveryParticipantRepository roomParticipantRepository;
    private final WebSocketSessionManager webSocketSessionManager;

    @Transactional
    public void send(SendChatDTO dto) throws IOException {
        for (WebSocketSession connectedSession : dto.getWebSocketSessions()) {
            connectedSession.sendMessage(dto.getMessage());
        }
        chatRepository.save(Chat.from(dto));
    }

    @Transactional
    public void closeParticipation(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);

        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsHost(roomParticipant.getRole(), "참여마감하기");

        room.closeParticipation();
        sendSystemMessage(room, user, SystemMessage.CLOSE_PARTICIPATION);
    }

    @Transactional
    public void completeRemit(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());

        validateIfRoomParticipantIsGuest(roomParticipant.getRole(), "송금완료");
        isRemitCompleted(roomParticipant);

        roomParticipant.completeRemit();
        sendSystemMessage(room, user, SystemMessage.COMPLETE_REMIT);
    }

    @Transactional
    public void completeDelivery(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);

        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsHost(roomParticipant.getRole(), "배달완료");

        room.completeDelivery();

        sendSystemMessage(room, user, SystemMessage.COMPLETE_DELIVERY);
    }

    @Transactional
    public void completeReceive(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());

        validateIfRoomParticipantIsGuest(roomParticipant.getRole(), "수령완료");
        isReceiveCompleted(roomParticipant);

        roomParticipant.completeReceive();
        sendSystemMessage(room, user, SystemMessage.COMPLETE_RECEIVE);
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

    private void sendSystemMessage(Room room, User user, SystemMessage systemMessage) throws IOException {
        Collection<WebSocketSession> webSocketSessions = webSocketSessionManager.getWebSocketSessions(room.getId());
        TextMessage textMessage = getTextMessage(systemMessage, user);
        send(new SendChatDTO(room, user, textMessage, ChatType.SYSTEM_MESSAGE, webSocketSessions));
    }

    private static TextMessage getTextMessage(SystemMessage systemMessage, User user) {
        Object[] systemMessageArgs = getSystemMessageArgs(systemMessage, user);
        return new TextMessage(systemMessage.getContent(systemMessageArgs));
    }

    private static Object[] getSystemMessageArgs(SystemMessage systemMessage, User user) {
        Object[] systemMessageArgs;
        if (systemMessage == SystemMessage.COMPLETE_DELIVERY) {
            systemMessageArgs = null;
        } else {
            systemMessageArgs = new Object[]{user.getName()};
        }
        return systemMessageArgs;
    }
}
