package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotParticipantException;
import fastcampus.team7.Livable_officener.repository.ChatRepository;
import fastcampus.team7.Livable_officener.repository.XChatRoomParticipantRepository;
import fastcampus.team7.Livable_officener.repository.XChatRoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChatServiceCompleteDeliveryTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private XChatRoomRepository roomRepository;
    @Mock
    private XChatRoomParticipantRepository roomParticipantRepository;

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

}