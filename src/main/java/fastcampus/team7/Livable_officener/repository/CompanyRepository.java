package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findCompaniesByBuildingName(String name);

    Optional<Company> findByName(String name);
}
