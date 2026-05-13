package org.sopt.korailtalk.global.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.sopt.korailtalk.global.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Objects;

/**
 * 컨트롤러 밖으로 올라온 예외를 클라이언트와 약속한 실패 응답으로 변환한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("[BusinessException] code={}, message={}",
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return toResponseEntity(errorCode);
    }

    /**
     * @RequestBody + @Valid 검증 실패를 필드별 실패 사유로 내려준다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        List<ErrorResponse.FieldError> errors = toFieldErrors(e);

        log.warn("[MethodArgumentNotValidException] {}", errors);

        return toResponseEntity(ErrorCode.INVALID_INPUT_VALUE, errors);
    }

    /**
     * @ModelAttribute 검증 실패를 필드별 실패 사유로 내려준다.
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        List<ErrorResponse.FieldError> errors = toFieldErrors(e);

        log.warn("[BindException] {}", errors);

        return toResponseEntity(ErrorCode.INVALID_INPUT_VALUE, errors);
    }

    /**
     * @RequestParam, @PathVariable 검증 실패를 필드별 실패 사유로 내려준다.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException e
    ) {
        List<ErrorResponse.FieldError> errors = e.getConstraintViolations()
                .stream()
                .map(error -> ErrorResponse.FieldError.of(
                        error.getPropertyPath().toString(),
                        Objects.toString(error.getInvalidValue(), ""),
                        error.getMessage()
                ))
                .toList();

        log.warn("[ConstraintViolationException] {}", errors);

        return toResponseEntity(ErrorCode.INVALID_INPUT_VALUE, errors);
    }

    /**
     * JSON 문법 오류나 요청 본문 타입 불일치를 처리한다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        log.warn("[HttpMessageNotReadableException] {}", e.getMessage());

        return toResponseEntity(ErrorCode.MESSAGE_NOT_READABLE);
    }

    /**
     * PathVariable, RequestParam 타입 변환 실패를 처리한다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        log.warn("[MethodArgumentTypeMismatchException] name={}, value={}",
                e.getName(),
                e.getValue()
        );

        return toResponseEntity(ErrorCode.INVALID_TYPE_VALUE);
    }

    /**
     * 필수 RequestParam 누락을 처리한다.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        log.warn("[MissingServletRequestParameterException] parameter={}", e.getParameterName());

        return toResponseEntity(ErrorCode.INVALID_REQUEST);
    }

    /**
     * 지원하지 않는 HTTP 메서드 요청을 처리한다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        log.warn("[HttpRequestMethodNotSupportedException] method={}", e.getMethod());

        return toResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 예상하지 못한 서버 오류는 내부 로그에만 상세 원인을 남긴다.
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[InternalServerError]", e);

        return toResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode));
    }

    private ResponseEntity<ErrorResponse> toResponseEntity(
            ErrorCode errorCode,
            List<ErrorResponse.FieldError> errors
    ) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.of(errorCode, errors));
    }

    private List<ErrorResponse.FieldError> toFieldErrors(BindException e) {
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorResponse.FieldError.of(
                        error.getField(),
                        Objects.toString(error.getRejectedValue(), ""),
                        error.getDefaultMessage()
                ))
                .toList();
    }
}
