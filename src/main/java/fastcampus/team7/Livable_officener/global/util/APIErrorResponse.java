package fastcampus.team7.Livable_officener.global.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class APIErrorResponse<T> {

    private int statusCode;
    private String message;

    public static <S> ResponseEntity<APIErrorResponse<S>> of(HttpStatus httpStatus, String message) {
        APIErrorResponse<S> response = new APIErrorResponse<>(httpStatus.value(), message);
        return new ResponseEntity<>(response, httpStatus);
    }
}
