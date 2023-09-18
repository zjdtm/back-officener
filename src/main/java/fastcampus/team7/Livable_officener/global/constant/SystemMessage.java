package fastcampus.team7.Livable_officener.global.constant;

public enum SystemMessage {
    CLOSE_PARTICIPATION("%s님이 참여한 배달의 모집이 완료되었습니다."),
    COMPLETE_TRANSFER("%s님이 송금을 완료했어요! 호스트님 확인해주세요.")
    ;

    private final String contentFormat;

    SystemMessage(String contentFormat) {
        this.contentFormat = contentFormat;
    }

    public String getContent(String userName) {
        return String.format(contentFormat, userName);
    }
}
