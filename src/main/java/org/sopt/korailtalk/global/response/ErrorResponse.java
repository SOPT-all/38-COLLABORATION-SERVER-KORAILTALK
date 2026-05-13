package org.sopt.korailtalk.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.sopt.korailtalk.global.exception.ErrorCode;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        List<FieldError> errors
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode.getCode(), errorCode.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, List<FieldError> errors) {
        return of(errorCode.getCode(), errorCode.getMessage(), errors);
    }

    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ErrorResponse of(String code, String message, List<FieldError> errors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }

    // FieldError는 Bean Validation처럼 필드별 실패 이유가 필요할 때만 응답에 포함한다.
    @Builder
    public record FieldError(
            String field,
            String value,
            String reason
    ) {

        public static FieldError of(String field, String value, String reason) {
            return FieldError.builder()
                    .field(field)
                    .value(value)
                    .reason(reason)
                    .build();
        }
    }
}
