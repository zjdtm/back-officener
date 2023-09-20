package fastcampus.team7.Livable_officener.global.sercurity;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            String message = e.getMessage();
            if (message.equals("시그니처 에러입니다.")) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("시그니처 에러입니다.");
            }
            //잘못된 타입의 토큰인 경우
            else if (message.equals("멜폼 에러입니다.")) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("멜폼 에러입니다.");
            }
            //토큰 만료된 경우
            else if (message.equals("익스파이어 에러입니다.")) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("익스파이어 에러입니다.");
            }
            //지원되지 않는 토큰인 경우
            else if (message.equals("일리걸아규먼트 에러입니다.")) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("일리걸아규먼트 에러입니다.");
            } else {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.getWriter().write("알 수 없는 에러입니다.");
            }
        }
    }
}
