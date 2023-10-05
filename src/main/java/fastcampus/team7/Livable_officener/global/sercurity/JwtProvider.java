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
import java.security.Principal;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private static final long exp = 1000L * 60 * 60;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    @Value("${jwt.secret.key}")
    private String salt;
    private Key secretKey;
    private JwtParser jwtParser;

    private final CustomUserDetailsService userDetailsService;
    private final RedisUtil redisUtil;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
        jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(secretKey)
                .build();
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

    public Authentication getAuthentication(String bearerToken) {
        Jws<Claims> claimsJws = getClaimsJwsByBearerToken(bearerToken);
        String username = claimsJws.getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getEmail(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Long getExpirationTime(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .getTime();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

//    public Authentication resolveAuthentication(StompHeaderAccessor accessor) {
//        String token = Objects.requireNonNull(accessor.getNativeHeader(AUTHORIZATION_HEADER)).get(0);
//        if (validateToken(token)) {
//            token = token.split(" ")[1].trim();
//            return getAuthentication(token);
//        }
//        throw new IllegalArgumentException("STOMP JWT 인증 실패");
//    }

    public Jws<Claims> getClaimsJwsByBearerToken(String bearerToken) {
        String accessToken = parseAccessToken(bearerToken);
        return getClaimsJwsByAccessToken(accessToken);
    }

    public String parseAccessToken(String bearerToken) {
        if (!bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
            throw new IllegalArgumentException("Bearer 토큰이 아닙니다.");
        }
        return bearerToken.split(" ")[1].trim();
    }

    public Jws<Claims> getClaimsJwsByAccessToken(String accessToken) {
        if (redisUtil.hasBlackList(accessToken)) {
            throw new AlreadyLogoutException(FilterExceptionCode.ALREADY_LOGGED_OUT);
        }

        try {
            Jws<Claims> claimsJws = jwtParser.parseClaimsJws(accessToken);
            if (claimsJws.getBody().getExpiration().before(new Date())) {
                throw new ExpiredJwtException(claimsJws.getHeader(), claimsJws.getBody(), "유효기간 지난 토큰");
            }
            return claimsJws;
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

    public Principal resolveUser(String accessToken) {
        final Jws<Claims> claimsJws = getClaimsJwsByAccessToken(accessToken);
        final String username = claimsJws.getBody().getSubject();
        return (Principal) userDetailsService.loadUserByUsername(username);
    }
}
