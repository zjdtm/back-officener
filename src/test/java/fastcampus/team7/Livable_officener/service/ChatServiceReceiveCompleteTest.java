package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.exception.AlreadyReceivedException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotGuestException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotParticipantException;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ChatServiceReceiveCompleteTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private DeliveryRepository roomRepository;
    @Mock
    private DeliveryParticipantRepository roomParticipantRepository;

    @Test
    @DisplayName("수령 완료시 채팅방 Id 없으면 예외 발생")
    void roomIdNotFoundTest() {
        //given
        Long WrongRoomId = 1L;
        User user = mock(User.class);

        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when//then
        customAssertThrow(WrongRoomId, user, NotFoundRoomException.class);
    }

    @Test
    @DisplayName("수령완료 시 해당 채팅방의 참여자가 아니면 예외발생")
    void isParticipantTest() {
        // given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);

        given(user.getId())
                .willReturn(1L);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(roomParticipantRepository.findRoomParticipant(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        // when, then
        customAssertThrow(roomId, user, UserIsNotParticipantException.class);
    }

    @Test
    @DisplayName("수령완료 시 게스트가 아니면 예외발생")
    void isGuestTest() {
        // given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);
        RoomParticipant roomParticipant = mock(RoomParticipant.class);

        given(user.getId())
                .willReturn(1L);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(roomParticipantRepository.findRoomParticipant(anyLong(), anyLong()))
                .willReturn(Optional.of(roomParticipant));
        given(roomParticipant.getRole())
                .willReturn(Role.HOST);

        // when, then
        customAssertThrow(roomId, user, UserIsNotGuestException.class);
    }

    @Test
    @DisplayName("이미 수령완료 했을 시 예외 발생")
    void alreadyReceive() {
        //given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);
        RoomParticipant roomParticipant = mock(RoomParticipant.class);

        given(user.getId())
                .willReturn(1L);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(roomParticipantRepository.findRoomParticipant(anyLong(), anyLong()))
                .willReturn(Optional.of(roomParticipant));
        given(roomParticipant.getRole())
                .willReturn(Role.GUEST);
        doThrow(AlreadyReceivedException.class)
                .when(roomParticipant).completeReceive();

        customAssertThrow(roomId, user, AlreadyReceivedException.class);
    }

    void customAssertThrow(Long roomId, User user,
                           Class<? extends Throwable> ex) {
        assertThatThrownBy(() ->
                sut.completeReceive(roomId, user))
                .isInstanceOf(ex);
    }
}
