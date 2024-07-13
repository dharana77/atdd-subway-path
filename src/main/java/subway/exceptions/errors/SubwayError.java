package subway.exceptions.errors;

import org.springframework.http.HttpStatus;

public enum SubwayError implements Error {
  INVALID_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, 400, "올바르지 않은 파라미터입니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, 404, "존재하지 않는 정보입니다.");

  private final HttpStatus status;
  private final int statusCode;
  private final String message;

  SubwayError(HttpStatus status, int statusCode, String message) {
    this.status = status;
    this.statusCode = statusCode;
    this.message = message;
  }

  @Override
  public HttpStatus getStatus() {
    return status;
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
