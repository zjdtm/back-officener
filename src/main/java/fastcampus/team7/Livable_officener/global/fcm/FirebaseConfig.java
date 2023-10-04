package fastcampus.team7.Livable_officener.global.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        final String fcmPath = "firebase/fc5-final-officener-firebase-adminsdk-clkdp-b13b9ec2b5.json";
        try (InputStream serviceAccount = new ClassPathResource(fcmPath).getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            List<FirebaseApp> apps = FirebaseApp.getApps();
            FirebaseApp firebaseApp;
            if (apps.isEmpty()) {
                firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                firebaseApp = apps.stream()
                        .filter(app -> app.getName().equals("web"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("이름과 일치하는 FirebaseApp이 존재하지 않음"));
            }
            return FirebaseMessaging.getInstance(firebaseApp);
        }
    }
}
