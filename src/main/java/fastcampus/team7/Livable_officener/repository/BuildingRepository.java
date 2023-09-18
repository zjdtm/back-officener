package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findBuildingsByNameContaining(String name);

    Optional<Building> findByName(String name);

}
