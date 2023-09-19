package fastcampus.team7.Livable_officener.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class DeliveryResponseDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    @Builder
    public static class RoomListResponseDTO {
        private Long roomId;
        private Long hostId;
        private String storeName;
        private String menuLink;
        private Long deliveryFee;
        private String tag;
        private Long attendees;
        private Long maxAttendees;
        private LocalDateTime deadLine;
        private String roomStatus;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter @Setter
    public static class PagedRoomListResponseDTO {
        private int currentPage;
        private int totalPage;
        private Long totalElements;
        private List<RoomListResponseDTO> rooms;
    }
}
