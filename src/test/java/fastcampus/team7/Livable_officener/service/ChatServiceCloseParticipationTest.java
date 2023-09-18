package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.exception.NotActiveRoomException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotHostException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotParticipantException;
import fastcampus.team7.Livable_officener.repository.XChatRoomParticipantRepository;
import fastcampus.team7.Livable_officener.repository.XChatRoomRepository;
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
class ChatServiceCloseParticipationTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private XChatRoomRepository roomRepository;
    @Mock
    private XChatRoomParticipantRepository roomParticipantRepository;

    @DisplayName("참여마감시 해당 ID에 해당하는 함께배달 없으면 예외발생")
    @Test
    void whenNoRoomOfId_thenThrowsException() {
        // given
        Long roomIdNotExist = 1L;
        User user = mock(User.class);

        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThrowsWhenCloseParticipation(roomIdNotExist, user, NotFoundRoomException.class);
    }

    @DisplayName("참여마감시 해당 함께배달의 참여자가 아니면 예외발생")
    @Test
    void whenNotParticipant_thenThrowsException() {
        // given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);

        given(user.getId())
                .willReturn(1L);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(roomParticipantRepository.findByRoomIdAndUserId(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThrowsWhenCloseParticipation(roomId, user, UserIsNotParticipantException.class);
    }

    @DisplayName("참여마감시 호스트가 아니면 예외발생")
    @Test
    void whenNotHost_thenThrowsException() {
        // given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);
        RoomParticipant roomParticipant = mock(RoomParticipant.class);

        given(user.getId())
                .willReturn(1L);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(roomParticipantRepository.findByRoomIdAndUserId(anyLong(), anyLong()))
                .willReturn(Optional.of(roomParticipant));
        given(roomParticipant.getRole())
                .willReturn(Role.GUEST);

        // when, then
        assertThrowsWhenCloseParticipation(
                roomId, user,
                UserIsNotHostException.class,
                "'참여마감하기' 요청은 호스트만 가능합니다.");
    }

    @DisplayName("참여마감시 함께배달이 활성상태가 아니면 예외발생")
    @Test
    void whenRoomIsNotActive_thenThrowsException() {
        // given
        Long roomId = 1L;
        User user = mock(User.class);
        Room room = mock(Room.class);
        RoomParticipant roomParticipant = mock(RoomParticipant.class);

        given(user.getId())
                .willReturn(1L);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(roomParticipantRepository.findByRoomIdAndUserId(anyLong(), anyLong()))
                .willReturn(Optional.of(roomParticipant));
        given(roomParticipant.getRole())
                .willReturn(Role.HOST);
        doThrow(NotActiveRoomException.class)
                .when(room).closeParticipation();

        // when, then
        assertThrowsWhenCloseParticipation(roomId, user, NotActiveRoomException.class);
    }

    private void assertThrowsWhenCloseParticipation(Long roomId, User user, Class<? extends Throwable> ex) {
        assertThrowsWhenCloseParticipation(roomId, user, ex, null);
    }

    private void assertThrowsWhenCloseParticipation(Long roomId, User user,
                                                    Class<? extends Throwable> ex,
                                                    String message) {
        var ast = assertThatThrownBy(() ->
                sut.closeParticipation(roomId, user))
                .isInstanceOf(ex);
        if (message != null) {
            ast.hasMessage(message);
        }
    }
}
