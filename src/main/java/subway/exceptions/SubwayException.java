package subway.exceptions;

import lombok.Getter;
import subway.exceptions.errors.SubwayError;

@Getter
public class SubwayException extends RuntimeException {
  private final SubwayError error;

  public SubwayException(SubwayError error) {
    this.error = error;
  }
}
