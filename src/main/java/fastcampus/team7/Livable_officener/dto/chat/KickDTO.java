package fastcampus.team7.Livable_officener.dto.chat;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class KickDTO {
    private Long kickedUserId;
}
