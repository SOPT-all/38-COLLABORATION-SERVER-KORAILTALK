package org.sopt.korailtalk.schedule.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.korailtalk.global.response.SuccessResponse;
import org.sopt.korailtalk.schedule.dto.response.ScheduleListResponse;
import org.sopt.korailtalk.schedule.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<SuccessResponse<ScheduleListResponse>> getSchedules() {
        ScheduleListResponse response = scheduleService.getSchedules();

        return ResponseEntity.ok(SuccessResponse.of("열차 일정 목록 조회에 성공했습니다.", response));
    }
}
