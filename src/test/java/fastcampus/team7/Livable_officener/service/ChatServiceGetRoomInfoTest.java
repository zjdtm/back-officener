package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.dto.chat.ChatroomInfoDTO;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.NotActiveRoomException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotParticipantException;
import fastcampus.team7.Livable_officener.repository.ChatRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryParticipantRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceGetRoomInfoTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private DeliveryRepository roomRepository;
    @Mock
    private DeliveryParticipantRepository roomParticipantRepository;

    @DisplayName("roomId에 해당하는 함께배달 존재하지 않으면 예외")
    @Test
    void whenNoRoomForRoomId_thenThrowsNotFoundRoomException() {
        // given
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getChatroomInfo(1L, mock(User.class)))
                .isInstanceOf(NotFoundRoomException.class);
    }

    @ParameterizedTest
    @EnumSource(value = RoomStatus.class, names = {"CLOSED", "TERMINATED"})
    @DisplayName("roomId에 해당하는 함께배달이 활성 상태가 아니면 예외")
    void whenRoomInactive_thenThrowsNotActiveRoomException(RoomStatus status) {
        // given
        Room room = mock(Room.class);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(room.getStatus())
                .willReturn(status);

        // when, then
        assertThatThrownBy(() -> sut.getChatroomInfo(1L, mock(User.class)))
                .isInstanceOf(NotActiveRoomException.class);
    }

    @DisplayName("roomId에 해당하는 함께배달의 참여자가 아니면 예외")
    @Test
    void whenNotParticipant_thenThrowsUserIsNotParticipantException() {
        // given
        User user = mock(User.class);
        Room room = mock(Room.class);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(room.getStatus())
                .willReturn(RoomStatus.ACTIVE);
        given(roomParticipantRepository.findRoomParticipant(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getChatroomInfo(1L, user))
                .isInstanceOf(UserIsNotParticipantException.class);
    }

    @DisplayName("성공")
    @Test
    void success() {
        // given
        User user = mock(User.class);
        Room room = mock(Room.class);
        Long roomId = 1L;
        RoomParticipant roomParticipant = mock(RoomParticipant.class);
        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.of(room));
        given(room.getStatus())
                .willReturn(RoomStatus.ACTIVE);
        given(roomParticipantRepository.findRoomParticipant(anyLong(), anyLong()))
                .willReturn(Optional.of(roomParticipant));

        // when
        ChatroomInfoDTO chatroomInfo = sut.getChatroomInfo(roomId, user);

        // then
        verify(roomParticipant, times(1)).resetUnreadCount();
        assertThat(roomParticipant.getUnreadCount()).isSameAs(0);
        verify(chatRepository, times(1)).findByRoomIdOrderByCreatedAtDesc(eq(roomId));
        verify(roomParticipantRepository, times(1)).findAllByRoomId(eq(roomId));
        assertThat(chatroomInfo).isNotNull();
    }
}
