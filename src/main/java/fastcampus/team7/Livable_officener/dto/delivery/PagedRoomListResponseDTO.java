package fastcampus.team7.Livable_officener.dto.delivery;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class PagedRoomListResponseDTO {
    private int currentPage;
    private int totalPage;
    private Long totalElements;
    private List<RoomListResponseDTO> rooms;
}
