package fastcampus.team7.Livable_officener.global.sercurity;

import fastcampus.team7.Livable_officener.global.constant.FilterExceptionCode;
import fastcampus.team7.Livable_officener.global.exception.AlreadyLogoutException;
import fastcampus.team7.Livable_officener.global.util.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    @Value("${jwt.secret.key}")
    private String salt;

    private Key secretKey;

    private final long exp = 1000L * 60 * 5;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final CustomUserDetailsService userDetailsService;

    private final RedisUtil redisUtil;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + exp))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getExpirationTime(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    /**
     *
     * @param request HttpServletRequest
     * @return "Authorization" 헤더에서 "Bearer {token} 형식으로 추출
     */
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    /**
     *
     * @param token "Bearer {token}" 형식
     * @return accessToken 을 추출
     */
    public String getBearerTokenPrefix(String token) {
        String accessToken = token.split(" ")[1].trim();
        return accessToken;
    }

    public boolean validateToken(String token) {
        try {
            if (!token.startsWith(BEARER_TOKEN_PREFIX)) {
                return false;
            }
            String accessToken = getBearerTokenPrefix(token);

            if (redisUtil.hasBlackList(accessToken)) {
                throw new AlreadyLogoutException(FilterExceptionCode.ALREADY_LOGGED_OUT);
            }

            Jws<Claims> claims = Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (SignatureException e) {
            log.info("JwtProvider SignatureException 예외 발생");
            throw new JwtException(FilterExceptionCode.WRONG_TYPE_TOKEN.getMessage());
        } catch (MalformedJwtException e) {
            log.info("JwtProvider MalformedJwtException 예외 발생");
            throw new JwtException(FilterExceptionCode.UNSUPPORTED_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("JwtProvider ExpiredJwtException 예외 발생");
            throw new JwtException(FilterExceptionCode.EXPIRED_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JwtProvider IllegalArgumentException 예외 발생");
            throw new JwtException(FilterExceptionCode.UNKNOWN_ERROR.getMessage());
        }
    }


}
