package fastcampus.team7.Livable_officener.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fastcampus.team7.Livable_officener.domain.*;
import fastcampus.team7.Livable_officener.dto.chat.*;
import fastcampus.team7.Livable_officener.global.constant.ChatType;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.*;
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
//        SimpleModule module = new SimpleModule();
//        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
//        objectMapper.registerModule(module);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
    }

    @Transactional
    public void send(SendChatDTO sendChatDTO) throws IOException {
        Room room = sendChatDTO.getRoom();
        User sender = sendChatDTO.getSender();
        SendPayloadDTO payloadDto = getSendPayloadDTO(sender, sendChatDTO.getMessage());

        sendFixedMessage(room, sender, payloadDto);
    }

    private SendPayloadDTO getSendPayloadDTO(User sender, TextMessage message) throws JsonProcessingException {
        Long senderId = sender.getId();
        String sendPayload = message.getPayload();

        SendPayloadDTO sendPayloadDTO = objectMapper.readValue(sendPayload, SendPayloadDTO.class);
        sendPayloadDTO.setSenderId(senderId);
        return sendPayloadDTO;
    }

    @Transactional
    public void enterChatroom(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);
        RoomParticipant newParticipant = getRoomParticipant(roomId, user.getId());

        // 고정 페이로드 생성
        String content = ENTER.getSystemMessageContent(user);
        SendPayloadDTO.Enter enterPayloadDto = new SendPayloadDTO.Enter(content, newParticipant);

        // DB에 저장
        chatRepository.save(Chat.from(room, user, enterPayloadDto));

        // 웹소켓 연결되지 않은 참여자의 읽지 않은 메시지 수 갱신
        incrementUnreadCountOfUnconnectedParticipant(room);

        // 수신자별로 amI 변경
        // 직렬화
        // TextMessage로 변환
        // 송신
        webSocketSessionManager.sendEnterMessageToAll(roomId, user, enterPayloadDto);
    }

    @Transactional
    public void closeParticipation(Long roomId, User user) throws IOException {
        Room room = getRoom(roomId);

        RoomParticipant participant = getRoomParticipant(roomId, user.getId());
        validateIfRoomParticipantIsHost(participant.getRole(), "참여마감하기");

        room.closeParticipation();

        sendDynamicSystemMessage(room, CLOSE_PARTICIPATION, user);
    }

    private void sendDynamicSystemMessage(Room room, ChatType messageType, User sender) throws IOException {
        // 빈 content의 SendPayloadDTO 생성
        SendPayloadDTO payloadDto = new SendPayloadDTO(messageType, sender.getId());

        // 채팅 메시지 DB에 저장
        chatRepository.save(Chat.from(room, sender, payloadDto));
        //
        //        // 웹소켓 연결되지 않은 참여자의 읽지 않은 메시지 수 갱신
        //        incrementUnreadCountOfUnconnectedParticipant(room);
        //
        //        // 웹소켓 연결된 각 회원에 대하여 시스템 메시지 내용 생성
        // SendPayloadDTO의 content 갱신
        // SendPayloadDTO 직렬화
        // TextMessage로 변환
        // 송신
        webSocketSessionManager.sendDynamicMessageToAll(room.getId(), payloadDto);
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
        Room room = getRoom(roomId);
        getRoomParticipant(roomId, user.getId());

        User reportedUser = validateReportedUser(reportDTO.getReportedUserId());

        validateReportFrequency(user, reportedUser);
        Report report = Report.createReport(reportDTO, reportedUser, user, room);

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
        sendFixedMessage(room, sender, payloadDto);
    }

    private void sendFixedMessage(Room room, User sender, SendPayloadDTO payloadDto) throws IOException {
        chatRepository.save(Chat.from(room, sender, payloadDto));

        incrementUnreadCountOfUnconnectedParticipant(room);

        TextMessage message = convertPayloadDtoToJsonTextMessage(payloadDto);
        webSocketSessionManager.sendToAll(room.getId(), message);
    }

    private TextMessage convertPayloadDtoToJsonTextMessage(SendPayloadDTO payloadDTO) throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(payloadDTO);
        return new TextMessage(payload);
    }

    /**
     * 함께배달 참여자 중 웹소켓세션이 연결되어있지 않은(=채팅 페이지를 벗어난) 참여자들의 unreadCount 증가
     */
    private void incrementUnreadCountOfUnconnectedParticipant(Room room) {
        roomParticipantRepository.findAllByRoomId(room.getId()).stream()
                .filter(participant -> webSocketSessionManager.nonexistent(room.getId(), participant.getUser()))
                .forEach(RoomParticipant::incrementUnreadCount);
    }

    @Transactional
    public ChatroomInfoDTO getChatroomInfo(Long roomId, User user) {
        Room room = getRoom(roomId);
        validateIfRoomIsActive(room);

        RoomParticipant participant = getRoomParticipant(roomId, user.getId());
        participant.resetUnreadCount();

        return createChatRoomInfoDTO(roomId, user.getId(), participant.getCreatedAt());
    }

    private static void validateIfRoomIsActive(Room room) {
        if (room.getStatus() != RoomStatus.ACTIVE) {
            throw new NotActiveRoomException();
        }
    }

    private ChatroomInfoDTO createChatRoomInfoDTO(Long roomId, Long userId, LocalDateTime joinedAt) {
        List<GetMessageDTO> messages = chatRepository.findByRoomIdAndJoinedAtAfterOrderByCreatedAtDesc(roomId, joinedAt).stream()
                .map(GetMessageDTO::from)
                .collect(Collectors.toList());
        List<GetParticipantDTO> members = roomParticipantRepository.findAllByRoomId(roomId).stream()
                .map(participant -> GetParticipantDTO.from(userId, participant))
                .collect(Collectors.toList());
        return new ChatroomInfoDTO(messages, members);
    }
}
