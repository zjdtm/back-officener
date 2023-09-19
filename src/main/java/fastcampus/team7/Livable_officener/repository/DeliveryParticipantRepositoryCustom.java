package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.RoomParticipant;

import java.util.Optional;

public interface DeliveryParticipantRepositoryCustom {

    Optional<RoomParticipant> findRoomParticipant(Long roomId, Long userId);
}
