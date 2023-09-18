package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.constant.Role;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotGuestException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotHostException;
import fastcampus.team7.Livable_officener.global.exception.UserIsNotParticipantException;
import fastcampus.team7.Livable_officener.repository.XChatRoomParticipantRepository;
import fastcampus.team7.Livable_officener.repository.XChatRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTransferCompleteTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private XChatRoomRepository roomRepository;
    @Mock
    private XChatRoomParticipantRepository roomParticipantRepository;


    @Test
    @DisplayName("송금 완료시 채팅방 Id 없으면 예외 발생")
    void roomIdNotFoundTest() {
        //given
        Long WrongRoomId = 1L;
        User user = mock(User.class);

        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when//then
        assertThatThrownBy(() ->
                sut.completeTransfer(WrongRoomId, user))
                .isInstanceOf(NotFoundRoomException.class);
    }

    @Test
    @DisplayName("송금완료 시 해당 채팅방의 참여자가 아니면 예외발생")
    void isParticipantTest() {
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
        assertThatThrownBy(() ->
                sut.completeTransfer(roomId, user))
                .isInstanceOf(UserIsNotParticipantException.class);
    }

}
