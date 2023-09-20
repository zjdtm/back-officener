package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.config.QueryDslConfig;
import fastcampus.team7.Livable_officener.domain.Building;
import fastcampus.team7.Livable_officener.domain.Company;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@Import(QueryDslConfig.class)
class BuildingRepositoryTest {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @BeforeEach
    public void init() {
        Building building1 = saveBuilding("미왕빌딩", "서울", "강남구", "강남대로", "364");
        saveCompanies(building1, "진회사", "A동 101호");
        saveCompanies(building1, "칠리버블", "A동 102호");
        saveCompanies(building1, "식스센스", "A동 103호");

        Building building2 = saveBuilding("테스트빌딩", "경기", "시흥시", "정왕동", "123");
        saveCompanies(building2, "테스트1", "B동 201호");
        saveCompanies(building2, "테스트2", "B동 202호");
        saveCompanies(building2, "테스트3", "B동 203호");
    }

    @AfterEach
    public void destroy() {
        companyRepository.deleteAll();
        buildingRepository.deleteAll();
    }

    @Test
    @DisplayName("[GET] /api/building?name={buildingName} - Repository 성공 테스트")
    void selectSuccessBuildingWithCompany() {

        // given
        final String keyword = "빌";

        // when
        List<Building> buildingList = buildingRepository.findBuildingsByNameContaining(keyword);
        List<Company> companyList = new ArrayList<>();

        for (Building building : buildingList) {
            List<Company> companiesByBuildingName = companyRepository.findCompaniesByBuildingName(building.getName());
            for (Company company : companiesByBuildingName) {
                companyList.add(company);
            }
        }

        // then
        Assertions.assertThat(buildingList.size()).isEqualTo(2);
        Assertions.assertThat(companyList.size()).isEqualTo(6);

    }

    @Test
    @DisplayName("[GET] /api/building?name={buildingName} - Repository 실패 테스트 (없는 빌딩을 검색할 경우)")
    void selectEmptyBuildingWithCompany() {

        // given
        final String keyword = "없는 키워드";

        // when
        List<Building> buildingList = buildingRepository.findBuildingsByNameContaining(keyword);
        List<Company> companyList = new ArrayList<>();

        for (Building building : buildingList) {
            companyRepository.findCompaniesByBuildingName(building.getName())
                    .stream().map(company -> companyList.add(company));
        }

        // then
        Assertions.assertThat(buildingList.size()).isEqualTo(0);
        Assertions.assertThat(companyList.size()).isEqualTo(0);

    }

    private void saveCompanies(Building building, String officeName, String officeUnit) {

        Company company = Company
                .builder()
                .building(building)
                .name(officeName)
                .address(officeUnit)
                .build();

        companyRepository.save(company);
    }

    private Building saveBuilding(String name, String region, String city, String street, String zipcode) {

        Building building = Building
                .builder()
                .name(name)
                .region(region)
                .city(city)
                .street(street)
                .zipcode(zipcode)
                .build();

        buildingRepository.save(building);
        return building;
    }
}
