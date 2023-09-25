package fastcampus.team7.Livable_officener.global.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FoodTag {
    PORKFEET("족발", "PORKFEET"),
    SUSHI("회", "SUSHI"),
    ZZIM("찜", "ZZIM"),
    PIZZA("피자", "PIZZA"),
    CHICKEN("치킨", "CHICKEN"),
    ASIAN("아시안", "ASIAN"),
    BAEKBAN("백반", "BAEKBAN"),
    BUNSIK("분식", "BUNSIK"),
    CAFE("카페", "CAFE"),
    CHINESE("중식", "CHINESE"),
    MEET("고기", "MEET"),
    DOSIRAK("도시락", "DOSIRAK");

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
