package org.sopt.korailtalk.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.korailtalk.global.response.SuccessResponse;
import org.sopt.korailtalk.reservation.dto.request.ReservationCreateRequest;
import org.sopt.korailtalk.reservation.dto.response.ReservationCreateResponse;
import org.sopt.korailtalk.reservation.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Reservation", description = "좌석 예약 API")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(
            summary = "좌석 예약 생성",
            description = "선택한 좌석을 간이 사용자 ID 기준으로 예약합니다."
    )
    @ApiResponse(
            responseCode = "201",
            description = "좌석 예약 성공",
            useReturnTypeSchema = true
    )
    @PostMapping("/reservations")
    public ResponseEntity<SuccessResponse<ReservationCreateResponse>> createReservation(
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        ReservationCreateResponse response = reservationService.createReservation(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.of("좌석 예약에 성공했습니다.", response));
    }
}
