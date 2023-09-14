package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Chat;
import fastcampus.team7.Livable_officener.dto.SendChatDTO;
import fastcampus.team7.Livable_officener.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatRepository chatRepository;

    @Transactional
    public void send(SendChatDTO dto) throws IOException {
        for (WebSocketSession connectedSession : dto.getSessionSet()) {
            connectedSession.sendMessage(dto.getMessage());
        }
        chatRepository.save(Chat.from(dto));
    }
}
