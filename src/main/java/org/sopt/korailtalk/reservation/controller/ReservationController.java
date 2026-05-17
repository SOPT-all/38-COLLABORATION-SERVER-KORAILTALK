package org.sopt.korailtalk.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.korailtalk.global.response.SuccessResponse;
import org.sopt.korailtalk.reservation.dto.request.ReservationCreateRequest;
import org.sopt.korailtalk.reservation.dto.response.ReservationCreateResponse;
import org.sopt.korailtalk.reservation.dto.response.ReservationHistoryListResponse;
import org.sopt.korailtalk.reservation.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "예매 내역 조회 API", description = "사용자의 예매 내역을 조회합니다.")

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예매 내역 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값")
    })
    @GetMapping("/users/{userId}/reservations")
    public ResponseEntity<SuccessResponse<ReservationHistoryListResponse>> getReservationHistory(
            @PathVariable Long userId
    ) {
        ReservationHistoryListResponse response =
                reservationService.getReservationHistory(userId);

        return ResponseEntity.ok(
                SuccessResponse.of("예매 내역 조회에 성공했습니다.", response)
        );
    }
}
