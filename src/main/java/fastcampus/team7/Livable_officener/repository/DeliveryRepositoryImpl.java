package fastcampus.team7.Livable_officener.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fastcampus.team7.Livable_officener.dto.delivery.RoomDetailDTO;
import fastcampus.team7.Livable_officener.global.constant.Role;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static fastcampus.team7.Livable_officener.domain.QChat.chat;
import static fastcampus.team7.Livable_officener.domain.QRoom.room;
import static fastcampus.team7.Livable_officener.domain.QRoomParticipant.roomParticipant;
import static fastcampus.team7.Livable_officener.dto.delivery.ChatRoomListResponseDTO.ChatRoomListDTO;

@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public RoomDetailDTO findRoomById(Long id) {
        return queryFactory
                .select(
                        Projections.constructor(RoomDetailDTO.class,
                                room.id.as("roomId"),
                                roomParticipant.user.id.as("hostId"),
                                roomParticipant.user.name.as("hostName"),
                                room.storeName,
                                room.menuLink,
                                room.deliveryFee,
                                room.tag,
                                room.bankName,
                                room.accountNumber,
                                room.deadline,
                                room.attendees,
                                room.maxAttendees,
                                room.description,
                                room.status,
                                ExpressionUtils.as(
                                        Expressions.asBoolean(
                                                findRoomHostById(id)
                                        ), "isJoin"
                                ),
                                room.createdAt,
                                room.updatedAt))
                .from(room)
                .innerJoin(roomParticipant).on(room.id.eq(roomParticipant.room.id))
                .where(
                        roomParticipant.room.id.eq(id),
                        roomParticipant.role.eq(Role.HOST)
                )
                .fetchOne();
    }

    public Boolean findRoomHostById(Long roomId) {
        return queryFactory
                .selectFrom(roomParticipant)
                .where(roomParticipant.room.id.eq(roomId))
                .fetchFirst() != null;
    }

    @Override
    public List<ChatRoomListDTO> findChatRoomList(Long userId) {
        return queryFactory
                .select(
                        Projections.constructor(ChatRoomListDTO.class,
                                room.id,
                                room.storeName,
                                chat.content,
                                roomParticipant.unreadCount.as("numUnreadMessages"),
                                room.tag
                        ))
                .from(room)
                .innerJoin(roomParticipant).on(
                        roomParticipant.room.id.eq(room.id),
                        roomParticipant.user.id.eq(userId)
                )
                .leftJoin(chat).on(room.id.eq(chat.room.id), chat.id.eq(
                        JPAExpressions.select(chat.id.max()).from(chat).where(chat.room.id.eq(room.id))
                ))
                .fetch();
    }
}
