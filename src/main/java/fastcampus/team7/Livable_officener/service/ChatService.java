package fastcampus.team7.Livable_officener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import fastcampus.team7.Livable_officener.domain.*;
import fastcampus.team7.Livable_officener.dto.chat.*;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.*;
import fastcampus.team7.Livable_officener.global.util.LocalDateTimeDeserializer;
import fastcampus.team7.Livable_officener.global.websocket.WebSocketSessionManager;
import fastcampus.team7.Livable_officener.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static fastcampus.team7.Livable_officener.global.constant.ChatType.*;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ObjectMapper objectMapper;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
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

        RoomParticipant participant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsHost(participant.getRole(), "참여마감하기");

        room.closeParticipation();

        sendFixedSystemMessage(room, CLOSE_PARTICIPATION, user);
    }

    @Transactional
    public void completeRemit(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);

        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsGuest(roomParticipant.getRole(), "송금완료");
        isRemitCompleted(roomParticipant);
        roomParticipant.completeRemit();

        sendFixedSystemMessage(room, COMPLETE_REMITTANCE, user);
    }

    @Transactional
    public void completeDelivery(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);

        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsHost(roomParticipant.getRole(), "배달완료");

        room.completeDelivery();

        sendFixedSystemMessage(room, COMPLETE_DELIVERY, user);
    }

    @Transactional
    public void completeReceive(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsGuest(roomParticipant.getRole(), "수령완료");
        isReceiveCompleted(roomParticipant);
        roomParticipant.completeReceive();

        sendFixedSystemMessage(room, COMPLETE_RECEIPT, user);
    }

    @Transactional
    public void kickRequest(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(room.getId(), user.getId());
        validateIfRoomParticipantIsGuest(roomParticipant.getRole(), "나가기요청");

        sendFixedSystemMessage(room, REQUEST_EXIT, user);
    }

    public void kick(Long roomId, User user, KickDTO kickDTO) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(room.getId(), user.getId());
        RoomParticipant pointedRoomParticipant = getRoomParticipant(room.getId(), kickDTO.getKickedUserId());
        validateIfRoomParticipantIsHost(roomParticipant.getRole(), "강퇴");

        isKicked(pointedRoomParticipant);
        User kickedUser = validateKickedUser(kickDTO.getKickedUserId());

        pointedRoomParticipant.guestKick();

        webSocketSessionManager.closeSessionForUser(roomId, kickedUser);
        roomParticipantRepository.delete(pointedRoomParticipant);

        sendFixedSystemMessage(room, KICK, user, kickedUser);
    }

    @Transactional
    public void exitChatRoom(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant roomParticipant = getRoomParticipant(roomId, user.getId());

        if (roomParticipant.getRole() == Role.HOST) {
            validateAllParticipantsCompletedRemitAndReceive(roomId);
            roomRepository.deleteById(roomId);
        } else if (roomParticipant.getRole() == Role.GUEST) {
            roomParticipant.guestExit();
            sendFixedSystemMessage(room, EXIT, user);
        }
    }

    @Transactional
    public Report createReport(Long roomId, User user, ReportDTO reportDTO) {
        getRoom(roomId);
        getRoomParticipant(roomId, user.getId());

        User reportedUser = validateReportedUser(reportDTO.getReportedUserId());

        validateReportFrequency(user, reportedUser);
        Report report = Report.createReport(reportDTO, reportedUser, user);

        return reportRepository.save(report);
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

    private User validateReportedUser(Long reportedUserId) {
        return userRepository.findById(reportedUserId)
                .orElseThrow(NotFoundReportedUserException::new);
    }

    private User validateKickedUser(Long KickedUserId) {
        return userRepository.findById(KickedUserId)
                .orElseThrow(NotFoundKickedUserException::new);
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

    private void validateReportFrequency(User reporter, User reportedUser) {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);

        Optional<Report> existingReport = reportRepository.findByReporterAndReportedUserAndCreatedAtIsAfter(reporter, reportedUser, todayStart);
        if (existingReport.isPresent()) {
            throw new AlreadyReportSameUserException();
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

    private static void isKicked(RoomParticipant pointedRoomParticipant) {
        if (pointedRoomParticipant.getKickedAt() != null) {
            throw new AlreadyKickedException();
        }
    }

    private void sendFixedSystemMessage(Room room, ChatType messageType, User... args) throws IOException {
        String content = messageType.getSystemMessageContent(args);
        User sender = args[0];
        SendPayloadDTO payloadDto = new SendPayloadDTO(messageType, content, sender.getId());
        sendMessage(room, sender, payloadDto);
    }

    private void sendMessage(Room room, User sender, SendPayloadDTO payloadDto) throws IOException {
        TextMessage message = convertPayloadDtoToJsonTextMessage(payloadDto);

        webSocketSessionManager.sendFixedMessage(room.getId(), message);
        chatRepository.save(Chat.from(room, sender, payloadDto));

        // 함께배달 참여자 중 웹소켓세션이 연결되어있지 않은(=채팅 페이지를 벗어난) 참여자들의 unreadCount 증가
        roomParticipantRepository.findAllByRoomId(room.getId()).stream()
                .filter(participant -> webSocketSessionManager.nonexistent(room.getId(), participant.getUser()))
                .forEach(RoomParticipant::incrementUnreadCount);
    }

    private static void validateIfRoomIsActive(Room room) {
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new NotActiveRoomException();
        }
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
