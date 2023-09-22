package fastcampus.team7.Livable_officener.global.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIDataResponse<T> {

    private final T data;

    public static <S> ResponseEntity<APIDataResponse<S>> of(HttpStatus httpStatus, S data) {
        APIDataResponse<S> response = new APIDataResponse<>(data);
        return new ResponseEntity<>(response, httpStatus);
    }

    public static <S> ResponseEntity<APIDataResponse<S>> empty(HttpStatus httpStatus) {
        return of(httpStatus, null);
    }
}
