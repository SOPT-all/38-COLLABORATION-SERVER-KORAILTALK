package org.sopt.korailtalk.schedule.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "열차 일정 목록 응답")
public record ScheduleListResponse(
        // ScheduleListResponse는 여러 개의 ScheduleResponse를 포함하는 리스트 형태로 반환된다.
        @Schema(description = "출발 시간 오름차순으로 정렬된 열차 일정 목록")
        List<ScheduleResponse> schedules
) {
    // ScheduleResponse는 각 열차 일정의 상세 정보를 담는 레코드로, ScheduleListResponse 내부에 정의했다.
    @Schema(description = "열차 일정 정보")
    public record ScheduleResponse(
            @Schema(description = "열차 일정 ID", example = "1")
            Long scheduleId,

            @Schema(description = "열차 종류", example = "KTX")
            String trainType,

            @Schema(description = "운행 열차 이름", example = "KTX 001")
            String trainName,

            @Schema(description = "출발 시간", example = "2026-05-10T06:00:00")
            LocalDateTime departureTime,

            @Schema(description = "도착 시간", example = "2026-05-10T08:25:00")
            LocalDateTime arrivalTime,

            @Schema(description = "일반실 가격", example = "59800")
            Integer generalPrice,

            @Schema(description = "특실 가격", example = "83700")
            Integer specialPrice,

            @Schema(description = "전체 좌석 매진 여부", example = "false")
            Boolean isSoldOut,

            @Schema(description = "콘센트 좌석 매진 여부", example = "false")
            Boolean isOutletSoldOut
    ) {
    }
}
