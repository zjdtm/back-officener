package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.UserElevator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserElevatorRepository extends JpaRepository<UserElevator,Long> {
    Optional<List<UserElevator>> findByUserId(Long id);
}
