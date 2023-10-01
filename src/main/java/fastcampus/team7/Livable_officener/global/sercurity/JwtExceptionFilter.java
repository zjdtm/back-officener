package fastcampus.team7.Livable_officener.global.sercurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import fastcampus.team7.Livable_officener.global.constant.FilterExceptionCode;
import fastcampus.team7.Livable_officener.global.exception.AlreadyLogoutException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {

            String message = e.getMessage();

            // 알 수 없는 에러입니다.
            if (FilterExceptionCode.UNKNOWN_ERROR.getMessage().equals(message)) {
                setResponse(response, FilterExceptionCode.UNKNOWN_ERROR);
            }
            // 잘못된 타입의 토큰인 경우
            else if (FilterExceptionCode.WRONG_TYPE_TOKEN.getMessage().equals(message)) {
                setResponse(response, FilterExceptionCode.WRONG_TYPE_TOKEN);
            }
            // 토큰 만료된 경우
            else if (FilterExceptionCode.EXPIRED_TOKEN.getMessage().equals(message)) {
                setResponse(response, FilterExceptionCode.EXPIRED_TOKEN);
            }
            // 지원되지 않는 토큰인 경우
            else if (FilterExceptionCode.UNSUPPORTED_TOKEN.getMessage().equals(message)) {
                setResponse(response, FilterExceptionCode.UNSUPPORTED_TOKEN);
            } else {
                setResponse(response, FilterExceptionCode.UNKNOWN_ERROR);
            }
        } catch (AlreadyLogoutException e) {
            setResponse(response, FilterExceptionCode.ALREADY_LOGGED_OUT);
        }

    }

    private void setResponse(HttpServletResponse response, FilterExceptionCode error) throws RuntimeException, IOException {

        log.info("[JwtExceptionFilter] 예외 발생 - " + error.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("utf-8");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("errorMessage", error.getMessage());
        String result = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().print(result);
    }
}
