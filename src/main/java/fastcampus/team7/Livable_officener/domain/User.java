package fastcampus.team7.Livable_officener.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class User extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Building building;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

}
