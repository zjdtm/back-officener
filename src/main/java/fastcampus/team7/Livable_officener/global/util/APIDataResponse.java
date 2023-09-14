package fastcampus.team7.Livable_officener.global.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class APIDataResponse<T> {

    private int statusCode;
    private String message;
    private T data;

    public static <S> ResponseEntity<APIDataResponse<S>> of(HttpStatus httpStatus, String message, S data) {
        APIDataResponse<S> response = new APIDataResponse<>(httpStatus.value(), message, data);
        return new ResponseEntity<>(response, httpStatus);
    }

    public static <S> ResponseEntity<APIDataResponse<S>> empty(HttpStatus httpStatus, String message) {
        APIDataResponse<S> response = new APIDataResponse<>(httpStatus.value(), message, null);
        return new ResponseEntity<>(response, httpStatus);
    }
}
