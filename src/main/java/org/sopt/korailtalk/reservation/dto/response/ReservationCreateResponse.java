package org.sopt.korailtalk.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "좌석 예약 생성 응답")
public record ReservationCreateResponse(
        @Schema(description = "열차 일정 ID", example = "1")
        Long scheduleId,

        @Schema(description = "간이 사용자 ID", example = "1")
        Long userId,

        @Schema(description = "예약된 좌석 번호 목록", example = "[1]")
        List<Integer> seatNumbers
) {
}
