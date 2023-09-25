package fastcampus.team7.Livable_officener.global.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FoodTag {
    CHICKEN("치킨", "CHICKEN"),
    DESSERT("디저트", "DESSERT"),
    MEET("고기", "MEET"),
    CHINESE("중식", "CHINESE"),
    JAPANESE("일식", "JAPANESE"),
    KOREAN("한식", "KOREAN"),
    BUNSIK("분식", "BUNSIK");

    private final String name;
    private final String code;

    public static FoodTag fromFoodNameToCode(String name) {
        for (FoodTag foodTag : values()) {
            if (foodTag.getName().equals(name) || foodTag.getCode().equals(name)) return foodTag;
        }

        throw new IllegalArgumentException("유효하지 않은 음식 태그 이름입니다 : " + name);
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
