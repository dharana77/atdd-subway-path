package subway.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class SubwayExceptionHandler {

  @ExceptionHandler(HttpClientErrorException.NotFound.class)
  public ResponseEntity<String> handleStationNotFoundException(HttpClientErrorException.NotFound e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }
}
