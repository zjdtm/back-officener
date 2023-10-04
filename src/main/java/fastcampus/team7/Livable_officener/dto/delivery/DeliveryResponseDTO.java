//package fastcampus.team7.Livable_officener.dto.delivery;
//
//import fastcampus.team7.Livable_officener.domain.Bank;
//import lombok.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class DeliveryResponseDTO {
//
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Getter @Setter
//    @Builder
//    public static class RoomListResponseDTO {
//        private Long roomId;
//        private Long hostId;
//        private String storeName;
//        private String menuLink;
//        private Long deliveryFee;
//        private String tag;
//        private Long attendees;
//        private Long maxAttendees;
//        private LocalDateTime deadLine;
//        private String roomStatus;
//        private LocalDateTime createdAt;
//        private LocalDateTime updatedAt;
//    }
//
//    @Getter @Setter
//    public static class PagedRoomListResponseDTO {
//        private int currentPage;
//        private int totalPage;
//        private Long totalElements;
//        private List<RoomListResponseDTO> rooms;
//    }
//
//    @RequiredArgsConstructor
//    @Getter @Setter
//    public static class BankDTO {
//        private String bankName;
//
//        public BankDTO(String bankName) {
//            this.bankName = bankName;
//        }
//    }
//
//    @RequiredArgsConstructor
//    @Getter @Setter
//    public static class BankListResponseDTO {
//        private List<Bank> bankList;
//
//        public BankListResponseDTO(List<Bank> bankList) {
//            this.bankList = bankList;
//        }
//    }
//}
