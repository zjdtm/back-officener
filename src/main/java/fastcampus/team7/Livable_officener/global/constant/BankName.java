package fastcampus.team7.Livable_officener.global.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BankName {
    KB("KB국민은행", "KB"),
    SHINHAN("신한은행", "SHINHAN"),
    WOORI("우리은행", "WOORI"),
    HANA("하나은행", "HANA"),
    SC("SC제일은행", "SC"),
    CITI("씨티은행", "CITI"),
    GYEONGNAM("경남은행", "GYEONGNAM"),
    GWANGJU("광주은행", "GWANGJU"),
    DAEGU("대구은행", "DAEGU"),
    BUSAN("부산은행", "BUSAN"),
    JEONBUK("전북은행", "JEONBUK"),
    JEJU("제주은행", "JEJU"),
    KIUP("기업은행", "KIUP"),
    NONGHYUP("농협", "NONGHYUP"),
    SUHYEOP("수협", "SUHYEOP"),
    SANUP("산업은행", "SANUP"),
    KBANK("케이뱅크", "KBANK"),
    KAKAO("카카오뱅크", "KAKAO"),
    TOSS("토스뱅크", "TOSS");

    private final String name;
    private final String code;

    public static BankName fromBankNameToCode(String name) {
        for (BankName bankName : values()) {
            if (bankName.getName().equals(name) || bankName.getCode().equals(name)) return bankName;
        }

        throw new IllegalArgumentException("유효하지 않은 은행 이름입니다 : " + name);
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
