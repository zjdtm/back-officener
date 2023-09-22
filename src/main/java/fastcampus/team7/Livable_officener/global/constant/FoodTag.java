package fastcampus.team7.Livable_officener.global.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FoodTag {
    CHICKEN("치킨"),
    DESSERT("디저트"),
    MEET("고기"),
    CHINESE("중식"),
    JAPANESE("일식"),
    KOREAN("한식"),
    BUNSIK("분식");

    private final String name;

    @JsonValue
    public String getName() {
        return name;
    }
}
