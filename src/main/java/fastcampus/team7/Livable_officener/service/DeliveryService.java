package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.repository.BankRepository;
import fastcampus.team7.Livable_officener.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final BankRepository bankRepository;
}
