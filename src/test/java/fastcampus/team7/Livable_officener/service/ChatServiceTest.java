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
class ChatServiceTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private XChatRoomRepository roomRepository;
    @Mock
    private XChatRoomParticipantRepository roomParticipantRepository;

    @Test
    void 참여마감시_채팅방Id없으면_예외발생() {
        // given
        Long roomIdNotExist = 1L;
        User user = mock(User.class);

        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThrowsWhenCloseParticipation(roomIdNotExist, user, NotFoundRoomException.class);
    }

    @Test
    void 참여마감시_해당채팅방의참여자가아니면_예외발생() {
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

    @Test
    void 참여마감시_호스트가아니면_예외발생() {
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

    @Test
    void 참여마감시_함께배달방이활성상태가아니면_예외발생() {
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