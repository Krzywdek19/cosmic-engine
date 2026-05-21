package pl.exceptionhandled.cosmicengine.common.api;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
        String code,
        String message,
        Map<String, String> fieldErrors,
        Instant timestamp
) {

    public static ApiErrorResponse validationError(Map<String, String> fieldErrors) {
        return new ApiErrorResponse(
                "VALIDATION_ERROR",
                "Request validation failed",
                fieldErrors,
                Instant.now()
        );
    }

    public static ApiErrorResponse badRequest(String message) {
        return new ApiErrorResponse(
                "BAD_REQUEST",
                message,
                Map.of(),
                Instant.now()
        );
    }

    public static ApiErrorResponse internalServerError() {
        return new ApiErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Unexpected server error",
                Map.of(),
                Instant.now()
        );
    }
}