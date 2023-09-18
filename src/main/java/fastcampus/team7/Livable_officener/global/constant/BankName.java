package fastcampus.team7.Livable_officener.global.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BankName {
    KB("KB국민은행"),
    SHINHAN("신한은행"),
    WOORI("우리은행"),
    HANA("하나은행"),
    SC("SC제일은행"),
    CITI("씨티은행"),
    GYEONGNAM("경남은행"),
    GWANGJU("광주은행"),
    DAEGU("대구은행"),
    BUSAN("부산은행"),
    JEONBUK("전북은행"),
    JEJU("제주은행"),
    KIUP("기업은행"),
    NONGHYUP("농협"),
    SUHYEOP("수협"),
    SANUP("산업은행"),
    KBANK("케이뱅크"),
    KAKAO("카카오뱅크"),
    TOSS("토스뱅크");

    private final String name;

    public String getName() {
        return name;
    }

}
