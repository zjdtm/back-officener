package fastcampus.team7.Livable_officener.dto.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ChatroomInfoDTO {

    private final List<GetMessageDTO> messages;
    private final List<GetParticipantDTO> members;
}
