package fastcampus.team7.Livable_officener.repository;

import fastcampus.team7.Livable_officener.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Room, Long>, DeliveryRepositoryCustom{


}
