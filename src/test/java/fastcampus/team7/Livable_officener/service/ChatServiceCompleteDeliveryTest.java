package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Chat;
import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.exception.AlreadyDeliveredException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotHostException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotParticipantException;
import fastcampus.team7.Livable_officener.global.websocket.WebSocketSessionManager;
import fastcampus.team7.Livable_officener.repository.ChatRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceCompleteDeliveryTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private DeliveryRepository roomRepository;
    @Mock
    private DeliveryParticipantRepository roomParticipantRepository;
    @Mock
    private WebSocketSessionManager webSocketSessionManager;

    @DisplayName("roomId에 해당하는 함께배달이 없으면 예외 발생")
    @Test
    void whenNoRoom_thenThrowsException() {
        // given
        Long nonExistentRoomId = 0L;
        User user = mock(User.class);

        // when, then
        assertThatThrownBy(() -> sut.completeDelivery(nonExistentRoomId, user))
                .isInstanceOf(NotFoundRoomException.class);
    }

    @DisplayName("roomId에 해당하는 함께배달의 참여자가 아니면 예외 발생")
    @Test
    void whenNotParticipant_thenThrowsException() {
        // given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);

        given(roomRepository.findById(roomId))
                .willReturn(Optional.of(room));

        // when, then
        assertThatThrownBy(() -> sut.completeDelivery(roomId, user))
                .isInstanceOf(UserIsNotParticipantException.class);
    }

    @DisplayName("호스트가 아니면 예외 발생")
    @Test
    void whenNotHost_thenThrowsException() {
        // given
        Long roomId = 1L;
        Long userId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);
        RoomParticipant roomParticipant = mock(RoomParticipant.class);

        given(roomRepository.findById(roomId))
                .willReturn(Optional.of(room));
        given(user.getId())
                .willReturn(userId);
        given(roomParticipantRepository.findRoomParticipant(roomId, userId))
                .willReturn(Optional.of(roomParticipant));
        given(roomParticipant.getRole())
                .willReturn(Role.GUEST);

        // when, then
        assertThatThrownBy(() -> sut.completeDelivery(roomId, user))
                .isInstanceOf(UserIsNotHostException.class)
                .hasMessageStartingWith("'배달완료'");
    }

    @DisplayName("이미 배달완료이면 예외 발생")
    @Test
    void whenAlreadyDelivered_thenThrowsException() {
        // given
        Long roomId = 1L;
        Long userId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);
        RoomParticipant roomParticipant = mock(RoomParticipant.class);

        given(roomRepository.findById(roomId))
                .willReturn(Optional.of(room));
        given(user.getId())
                .willReturn(userId);
        given(roomParticipantRepository.findRoomParticipant(roomId, userId))
                .willReturn(Optional.of(roomParticipant));
        given(roomParticipant.getRole())
                .willReturn(Role.HOST);
        doThrow(AlreadyDeliveredException.class)
                .when(room).completeDelivery();

        // when, then
        assertThatThrownBy(() -> sut.completeDelivery(roomId, user))
                .isInstanceOf(AlreadyDeliveredException.class);
    }

    @DisplayName("배달완료시 예외 발생 안함")
    @Test
    void whenCompleteDelivery_thenPass() throws IOException {
        // given
        Long roomId = 1L;
        Long userId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);
        RoomParticipant roomParticipant = mock(RoomParticipant.class);

        given(roomRepository.findById(roomId))
                .willReturn(Optional.of(room));
        given(user.getId())
                .willReturn(userId);
        given(roomParticipantRepository.findRoomParticipant(roomId, userId))
                .willReturn(Optional.of(roomParticipant));
        given(roomParticipant.getRole())
                .willReturn(Role.HOST);
        given(room.getId())
                .willReturn(roomId);

        // when, then
        sut.completeDelivery(roomId, user);

        verify(room, times(1)).completeDelivery();
        verify(webSocketSessionManager, times(1)).getWebSocketSessions(roomId);
        verify(chatRepository, times(1)).save(any(Chat.class));
    }
}
