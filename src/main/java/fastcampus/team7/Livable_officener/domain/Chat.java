package fastcampus.team7.Livable_officener.domain;

import fastcampus.team7.Livable_officener.global.constant.ChatType;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Chat extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    private String content;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ChatType type;

}
