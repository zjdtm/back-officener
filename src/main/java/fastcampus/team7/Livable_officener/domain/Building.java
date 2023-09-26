package fastcampus.team7.Livable_officener.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.StringJoiner;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
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

    public String getAddress() {
        StringJoiner sj = new StringJoiner(" ");
        sj.add(this.region);
        sj.add(this.city);
        sj.add(this.street);
        sj.add(this.zipcode);
        return sj.toString();
    }

}
