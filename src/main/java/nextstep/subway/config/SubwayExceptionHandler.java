package nextstep.subway.config;

import nextstep.subway.exception.InvalidDistanceBetweenStationsException;
import nextstep.subway.exception.NoPathException;
import nextstep.subway.exception.SectionRegistrationException;
import nextstep.subway.exception.SectionRemovalException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class SubwayExceptionHandler {

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> entityNotFoundException(EntityNotFoundException e) {
        final ErrorResponse errorResponse = new ErrorResponse(EntityNotFoundException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    public ResponseEntity<ErrorResponse> emptyResultDataAccessException(EmptyResultDataAccessException e) {
        final ErrorResponse errorResponse = new ErrorResponse(EmptyResultDataAccessException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({SectionRegistrationException.class})
    public ResponseEntity<ErrorResponse> sectionRegistrationException(SectionRegistrationException e) {
        final ErrorResponse errorResponse = new ErrorResponse(SectionRegistrationException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({InvalidDistanceBetweenStationsException.class})
    public ResponseEntity<ErrorResponse> invalidDistanceBetweenStationsException(InvalidDistanceBetweenStationsException e) {
        final ErrorResponse errorResponse = new ErrorResponse(InvalidDistanceBetweenStationsException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({SectionRemovalException.class})
    public ResponseEntity<ErrorResponse> sectionRemovalException(SectionRemovalException e) {
        final ErrorResponse errorResponse = new ErrorResponse(SectionRemovalException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<ErrorResponse> noSuchElementException(NoSuchElementException e) {
        final ErrorResponse errorResponse = new ErrorResponse(NoSuchElementException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> illegalArgumentException(IllegalArgumentException e) {
        final ErrorResponse errorResponse = new ErrorResponse(IllegalArgumentException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({NoPathException.class})
    public ResponseEntity<ErrorResponse> noPathException(NoPathException e) {
        final ErrorResponse errorResponse = new ErrorResponse(NoPathException.class.getName(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

}