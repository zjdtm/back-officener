package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.User;
import fastcampus.team7.Livable_officener.global.exception.NotFoundRoomException;
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
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService sut;
    @Mock
    private XChatRoomRepository roomRepository;

    @Test
    void 참여마감시_채팅방Id없으면_예외발생() {
        // given
        Long roomIdNotExist = 1L;
        User user = mock(User.class);

        given(roomRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() ->
                sut.closeToTakePartIn(roomIdNotExist, user))
                .isInstanceOf(NotFoundRoomException.class);
    }

}