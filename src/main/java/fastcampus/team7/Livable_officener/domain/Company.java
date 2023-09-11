package fastcampus.team7.Livable_officener.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Getter
@Entity
public class Company extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private Building building;

    @Column(nullable = false)
    private String name;

}
