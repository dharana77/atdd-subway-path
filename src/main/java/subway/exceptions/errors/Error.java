package subway.exceptions.errors;

import org.springframework.http.HttpStatus;

public interface Error {
  HttpStatus getStatus();
  int getStatusCode();
  String getMessage();
}
