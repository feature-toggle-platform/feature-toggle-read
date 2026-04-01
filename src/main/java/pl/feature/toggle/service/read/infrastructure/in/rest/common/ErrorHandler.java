package pl.feature.toggle.service.read.infrastructure.in.rest.common;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.feature.toggle.service.read.domain.exception.EnvironmentNotFoundException;
import pl.feature.toggle.service.read.domain.exception.FeatureToggleNotFoundException;
import pl.feature.toggle.service.read.domain.exception.ProjectNotFoundException;
import pl.feature.toggle.service.web.ErrorCode;
import pl.feature.toggle.service.web.ErrorResponse;
import pl.feature.toggle.service.web.correlation.CorrelationProvider;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static pl.feature.toggle.service.web.ErrorCode.*;

@RestControllerAdvice
@AllArgsConstructor
class ErrorHandler {

    private final CorrelationProvider correlationProvider;

    @ExceptionHandler(exception = FeatureToggleNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(FeatureToggleNotFoundException exception) {
        var errorResponse = createErrorResponse(FEATURE_TOGGLE_NOT_FOUND, exception);
        return ResponseEntity
                .status(NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(exception = ProjectNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(ProjectNotFoundException exception) {
        var errorResponse = createErrorResponse(PROJECT_NOT_FOUND, exception);
        return ResponseEntity
                .status(NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(exception = EnvironmentNotFoundException.class)
    ResponseEntity<ErrorResponse> handle(EnvironmentNotFoundException exception) {
        var errorResponse = createErrorResponse(ENVIRONMENT_NOT_FOUND, exception);
        return ResponseEntity
                .status(NOT_FOUND)
                .body(errorResponse);
    }

    private ErrorResponse createErrorResponse(ErrorCode errorCode, Exception e) {
        return ErrorResponse.from(errorCode, e, correlationProvider.current());
    }
}
