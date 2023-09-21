package fastcampus.team7.Livable_officener.global.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class APIErrorResponse {

    private final String errorMessage;

    public static ResponseEntity<APIErrorResponse> of(HttpStatus httpStatus, String errorMessage) {
        APIErrorResponse response = new APIErrorResponse(errorMessage);
        return new ResponseEntity<>(response, httpStatus);
    }
}
