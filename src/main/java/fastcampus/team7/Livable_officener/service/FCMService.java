package fastcampus.team7.Livable_officener.service;

import fastcampus.team7.Livable_officener.dto.fcm.FCMRegistrationDTO;
import fastcampus.team7.Livable_officener.global.fcm.FCMTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FCMService {

    private final FCMTokenRepository fcmTokenRepository;

    @Transactional
    public void registerFcmToken(FCMRegistrationDTO dto) {
        fcmTokenRepository.save(dto);
    }
}
