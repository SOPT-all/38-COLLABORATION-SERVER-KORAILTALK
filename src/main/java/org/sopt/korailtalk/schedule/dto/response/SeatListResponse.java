package org.sopt.korailtalk.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SeatListResponse(
        @Schema(description = "열차 일정 ID")
        Long scheduleId,

        @Schema(description = "예약된 좌석 번호")
        List<Integer> reservedSeats,

        @Schema(description = "콘센트 좌석 번호")
        List<Integer> outletSeats
) {
}
