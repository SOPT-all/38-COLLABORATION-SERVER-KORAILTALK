package org.sopt.korailtalk.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_003", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_004", "서버 내부 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_005", "요청 값의 타입이 올바르지 않습니다."),
    MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "COMMON_006", "요청 본문(JSON) 형식이 올바르지 않습니다."),

    // Schedule
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_001", "존재하지 않는 열차 일정입니다."),

    // Seat
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "SEAT_001", "존재하지 않는 좌석입니다."),
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "SEAT_002", "이미 예약된 좌석입니다."),
    SEAT_BLOCKED(HttpStatus.CONFLICT, "SEAT_003", "선택할 수 없는 좌석입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
