package fastcampus.team7.Livable_officener.controller;

import fastcampus.team7.Livable_officener.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/room")
public class DeliveryController {

    private final DeliveryService deliveryService;


}
