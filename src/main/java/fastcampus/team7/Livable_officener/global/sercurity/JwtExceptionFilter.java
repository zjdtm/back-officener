package fastcampus.team7.Livable_officener.global.sercurity;

import fastcampus.team7.Livable_officener.global.constant.JwtExceptionCode;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            log.info("JwtExceptionFilter 예외 발생");

            String message = e.getMessage();

            // TODO : 응답 값에 맞게 DTO 형태로 바꿔줘야 함
            if (JwtExceptionCode.UNKNOWN_ERROR.getMessage().equals(message)) {
                setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
            }
            //잘못된 타입의 토큰인 경우
            else if (JwtExceptionCode.WRONG_TYPE_TOKEN.getMessage().equals(message)) {
                setResponse(response, JwtExceptionCode.WRONG_TYPE_TOKEN);
            }
            //토큰 만료된 경우
            else if (JwtExceptionCode.EXPIRED_TOKEN.getMessage().equals(message)) {
                setResponse(response, JwtExceptionCode.EXPIRED_TOKEN);
            }
            //지원되지 않는 토큰인 경우
            else if (JwtExceptionCode.UNSUPPORTED_TOKEN.getMessage().equals(message)) {
                setResponse(response, JwtExceptionCode.UNSUPPORTED_TOKEN);
            } else {
                setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
            }
        }

    }

    private void setResponse(HttpServletResponse response, JwtExceptionCode errorMessage) throws RuntimeException, IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("utf-8");

        response.getWriter().print(errorMessage.getMessage());
    }
}
