package fastcampus.team7.Livable_officener.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fastcampus.team7.Livable_officener.domain.RoomParticipant;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static fastcampus.team7.Livable_officener.domain.QRoomParticipant.roomParticipant;

@RequiredArgsConstructor
public class DeliveryParticipantRepositoryImpl implements DeliveryParticipantRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<RoomParticipant> findRoomParticipant(Long roomId, Long userId) {
        return Optional.ofNullable(queryFactory.selectFrom(roomParticipant)
                .where(
                        roomParticipant.room.id.eq(roomId),
                        roomParticipant.user.id.eq(userId)
                )
                .fetchOne());
    }
}
