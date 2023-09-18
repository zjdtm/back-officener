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
import fastcampus.team7.Livable_officener.repository.XChatRoomParticipantRepository;
import fastcampus.team7.Livable_officener.repository.XChatRoomRepository;
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
    private final XChatRoomRepository roomRepository;
    private final XChatRoomParticipantRepository roomParticipantRepository;
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
        validateIfUserIsHost(roomId, user.getId());
        room.closeParticipation();
        sendCloseSystemMessage(room, user);
    }

    @Transactional
    public void completeTransfer(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant= getRoomParticipant(roomId, user.getId());
        if(roomParticipant.getRole() != Role.GUEST) {
            throw new UserIsNotGuestException("송금완료");
        }
        if( roomParticipant.getTransferredAt() != null ) {
            throw new AlreadyTransferredException();
        }
        roomParticipant.completeTransfer();
        sendCompleteTransferSystemMessage(room, user);
    }

    private Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(NotFoundRoomException::new);
    }

    private void validateIfUserIsHost(Long roomId, Long userId) {
        RoomParticipant roomParticipant = getRoomParticipant(roomId, userId);
        validateIfRoomParticipantIsHost(roomParticipant.getRole());
    }

    private RoomParticipant getRoomParticipant(Long roomId, Long userId) {
        return roomParticipantRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(UserIsNotParticipantException::new);
    }

    private static void validateIfRoomParticipantIsHost(Role role) {
        if (role != Role.HOST) {
            throw new UserIsNotHostException("참여마감하기");
        }
    }

    private void sendCloseSystemMessage(Room room, User user) throws IOException {
        Collection<WebSocketSession> webSocketSessions = webSocketSessionManager.getWebSocketSessions(room.getId());
        TextMessage textMessage = new TextMessage(SystemMessage.CLOSE.getContent(user.getName()));
        send(new SendChatDTO(room, user, textMessage, ChatType.SYSTEM_MESSAGE, webSocketSessions));
    }

    private void sendCompleteTransferSystemMessage(Room room, User user) throws IOException {
        Collection<WebSocketSession> webSocketSessions = webSocketSessionManager.getWebSocketSessions(room.getId());
        TextMessage textMessage = new TextMessage(SystemMessage.COMPLETE_TRANSFER.getContent(user.getName()));
        send(new SendChatDTO(room, user, textMessage, ChatType.SYSTEM_MESSAGE, webSocketSessions));
    }
}
