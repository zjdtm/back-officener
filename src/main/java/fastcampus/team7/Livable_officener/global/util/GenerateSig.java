package fastcampus.team7.Livable_officener.global.util;

import org.springframework.beans.factory.annotation.Value;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;


public class GenerateSig {

    public static String generateSignature(String apiKey, String apiSecret) {
        try {
            if (apiKey == null || apiSecret == null) {
                System.out.println(apiKey + apiSecret);
                throw new IllegalArgumentException("apiKey 또는 apiSecret이 null입니다.");
            }

            String timestamp = generateTimestamp();
            String nonce = generateNonce();
            String data = timestamp + nonce;
            String algorithm = "HmacSHA256";

            byte[] secretBytes = apiSecret.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretBytes, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(secretKeySpec);

            byte[] signatureBytes = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(signatureBytes);
        }
        catch (Exception e){
            throw new RuntimeException("서명 생성 중 오류 발생", e);
        }
    }

    public static String generateTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    public static String generateNonce() {
        byte[] nonceBytes = new byte[16];
        new SecureRandom().nextBytes(nonceBytes);
        return Base64.getEncoder().encodeToString(nonceBytes);
    }

}
