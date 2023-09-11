package fastcampus.team7.Livable_officener.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Entity
public class Building extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, length = 5)
    private String zipcode;

}
