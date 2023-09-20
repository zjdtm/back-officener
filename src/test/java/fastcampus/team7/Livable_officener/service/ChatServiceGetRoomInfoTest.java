package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
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

}