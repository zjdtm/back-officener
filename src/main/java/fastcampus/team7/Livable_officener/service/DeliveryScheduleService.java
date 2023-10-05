package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.domain.Room;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DeliveryScheduleService {

    private final DeliveryRepository deliveryRepository;

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void closeParticipationAfterDeadline() {
        deliveryRepository.findByDeadlineAfterNow()
                .forEach(Room::closeParticipation);
    }
}
