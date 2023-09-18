package fastcampus.team7.Livable_officener.global.constant;

public enum SystemMessage {
    CLOSE("%s님이 참여한 배달의 모집이 완료되었습니다.");

    private final String contentFormat;

    SystemMessage(String contentFormat) {
        this.contentFormat = contentFormat;
    }

    public String getContent(String userName) {
        return String.format(contentFormat, userName);
    }
}
