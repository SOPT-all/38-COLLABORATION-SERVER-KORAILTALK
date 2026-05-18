package org.sopt.korailtalk.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Schema(description = "좌석 예약 생성 요청")
public record ReservationCreateRequest(
        @Schema(description = "열차 일정 ID", example = "1")
        @NotNull(message = "스케줄 ID는 필수입니다.")
        @Positive(message = "스케줄 ID는 양수여야 합니다.")
        Long scheduleId,

        @Schema(description = "간이 사용자 ID", example = "1")
        @NotNull(message = "사용자 ID는 필수입니다.")
        @Positive(message = "사용자 ID는 양수여야 합니다.")
        Long userId,

        @Schema(description = "예약할 좌석 번호 목록", example = "[1]")
        @NotEmpty(message = "좌석 번호는 최소 1개 이상 선택해야 합니다.")
        @UniqueElements(message = "좌석 번호는 중복될 수 없습니다.")
        List<@NotNull(message = "좌석 번호는 필수입니다.")
                @Min(value = 1, message = "좌석 번호는 1 이상이어야 합니다.")
                @Max(value = 72, message = "좌석 번호는 72 이하여야 합니다.") Integer> seatNumbers
) {
}
