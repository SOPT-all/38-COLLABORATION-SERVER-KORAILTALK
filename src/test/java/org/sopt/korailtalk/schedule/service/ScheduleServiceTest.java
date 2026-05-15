package org.sopt.korailtalk.schedule.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.korailtalk.reservation.repository.ReservationSeatRepository;
import org.sopt.korailtalk.schedule.dto.response.ScheduleListResponse;
import org.sopt.korailtalk.schedule.repository.ScheduleRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ReservationSeatRepository reservationSeatRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @Test
    @DisplayName("열차 일정이 없으면 빈 목록을 반환한다")
    void getSchedulesReturnsEmptyListWhenNoScheduleExists() {
        when(scheduleRepository.findAllByOrderByDepartureTimeAsc()).thenReturn(List.of());

        ScheduleListResponse response = scheduleService.getSchedules();

        assertThat(response.schedules()).isEmpty();
    }
}
