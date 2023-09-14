package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Building;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findBuildingsByNameContaining(String name);

}
