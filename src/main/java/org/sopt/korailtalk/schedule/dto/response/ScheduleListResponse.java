package org.sopt.korailtalk.schedule.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ScheduleListResponse(
        // ScheduleListResponse는 여러 개의 ScheduleResponse를 포함하는 리스트 형태로 반환된다.
        List<ScheduleResponse> schedules
) {
    // ScheduleResponse는 각 열차 일정의 상세 정보를 담는 레코드로, ScheduleListResponse 내부에 정의했다.
    public record ScheduleResponse(
            Long scheduleId,
            String trainType,
            String trainName,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            Integer generalPrice,
            Integer specialPrice,
            Boolean isSoldOut,
            Boolean isOutletSoldOut
    ) {
    }
}
