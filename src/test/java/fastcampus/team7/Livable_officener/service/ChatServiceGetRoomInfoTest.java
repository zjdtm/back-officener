package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.RoomStatus;
import fastcampus.team7.Livable_officener.global.exception.NotActiveRoomException;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import fastcampus.team7.Livable_officener.repository.XChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChatServiceGetRoomInfoTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private XChatRoomRepository roomRepository;

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
}