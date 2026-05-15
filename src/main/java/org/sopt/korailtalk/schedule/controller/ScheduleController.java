package org.sopt.korailtalk.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sopt.korailtalk.global.response.SuccessResponse;
import org.sopt.korailtalk.schedule.dto.response.ScheduleListResponse;
import org.sopt.korailtalk.schedule.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
@Tag(name = "Schedule", description = "열차 일정 API")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final SeatService seatService;

    @Operation(
            summary = "열차 일정 목록 조회",
            description = "열차 일정 목록을 출발 시간 오름차순으로 조회합니다. 조회 가능한 열차 일정이 없으면 빈 목록을 반환합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "열차 일정 목록 조회 성공",
            useReturnTypeSchema = true
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<ScheduleListResponse>> getSchedules() {
        ScheduleListResponse response = scheduleService.getSchedules();

        return ResponseEntity.ok(SuccessResponse.of("열차 일정 목록 조회에 성공했습니다.", response));
    }

    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<SeatListResponse> getSeats(
            @PathVariable Long scheduleId
    ) {
        SeatListResponse response = seatService.getSeats(scheduleId);
        return ResponseEntity.ok(response);
    }
}
