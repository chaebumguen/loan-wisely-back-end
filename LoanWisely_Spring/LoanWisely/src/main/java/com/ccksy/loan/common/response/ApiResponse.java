package com.ccksy.loan.common.response;

import java.time.LocalDateTime;

import com.ccksy.loan.common.exception.ErrorCode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
//생성자를 외부에서 직접 호출하지 못하도록 제한
//→ 반드시 success() / error() 팩토리 메서드만 사용하게 강제
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//Jackson 역직렬화 및 상속 대비용 기본 생성자
//→ 외부 new 호출은 불가
public class ApiResponse<T> {

    private boolean success;        // 성공 여부
    private String code;            // SUCCESS 또는 ErrorCode
    private String message;         // 응답 메시지
    private T data;                 // 실제 응답 데이터
    private LocalDateTime timestamp;

    /* =========================
     * Success Response
     * ========================= */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                "요청이 정상 처리되었습니다.",
                data,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                message,
                data,
                LocalDateTime.now()
        );
    }

    /* =========================
     * Error Response
     * ========================= */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                null,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return new ApiResponse<>(
                false,
                errorCode.getCode(),
                customMessage,
                null,
                LocalDateTime.now()
        );
    }
}
