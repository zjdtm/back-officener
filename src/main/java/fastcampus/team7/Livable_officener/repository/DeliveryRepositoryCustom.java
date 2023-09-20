package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.dto.RoomDetailDTO;

import java.util.List;

import static fastcampus.team7.Livable_officener.dto.ChatRoomListResponseDTO.ChatRoomListDTO;

public interface DeliveryRepositoryCustom {
    RoomDetailDTO findRoomById(Long id);

    Boolean findRoomHostById(Long roomId);

    List<ChatRoomListDTO> findChatRoomList(Long userId);
}
