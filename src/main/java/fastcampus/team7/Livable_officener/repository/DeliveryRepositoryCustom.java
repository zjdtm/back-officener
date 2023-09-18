package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.dto.RoomDetailDTO;

public interface DeliveryRepositoryCustom {
    RoomDetailDTO findRoomById(Long id);
    Boolean findRoomHostById(Long roomId);
}
