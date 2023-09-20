package fastcampus.team7.Livable_officener.dto;

import fastcampus.team7.Livable_officener.global.constant.FoodTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class ChatRoomListResponseDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ChatRoomListDTO {
        private Long chatRoomId;
        private String storeName;
        private String recentMessage;
        private Long numUnreadMessages;
        private FoodTag foodTag;

        public static ChatRoomListDTO of(ChatRoomListDTO chat) {
            return new ChatRoomListDTO(chat.getChatRoomId(), chat.getStoreName(),
                    chat.recentMessage, chat.numUnreadMessages, chat.getFoodTag());
        }
    }

    @Getter
    public static class MyChatListResponseDTO {
        List<ChatRoomListDTO> chats;

        public void listOf(List<ChatRoomListDTO> chatList) {
            this.chats = chatList.stream().map(ChatRoomListDTO::of).collect(Collectors.toList());
        }
    }
}
