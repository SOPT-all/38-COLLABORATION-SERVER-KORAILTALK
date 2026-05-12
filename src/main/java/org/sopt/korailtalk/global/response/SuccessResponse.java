package org.sopt.korailtalk.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SuccessResponse<T>(
        String message,
        T data
) {

    // Swagger의 @ApiResponse와 이름 충돌을 피하기 위해 성공 응답은 SuccessResponse로 구분한다.
    public static <T> SuccessResponse<T> of(String message, T data) {
        return SuccessResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }
}
