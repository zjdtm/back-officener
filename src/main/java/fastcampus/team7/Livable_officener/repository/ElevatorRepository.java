package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Elevator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ElevatorRepository extends JpaRepository<Elevator,Long> {

    Optional<Elevator> findById(Long id);

}
