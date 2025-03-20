package jinviz.share_depot_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 표준 응답 형식
 * @param <T> 응답 데이터 타입
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String message;
    private T data;

    /**
     * 성공 응답 생성
     * @param data 응답 데이터
     * @param message 응답 메시지
     * @return ApiResponse 객체
     * @param <T> 데이터 타입
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .build();
    }

    /**
     * 성공 응답 생성 (기본 메시지)
     * @param data 응답 데이터
     * @return ApiResponse 객체
     * @param <T> 데이터 타입
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "success");
    }

    /**
     * 에러 응답 생성
     * @param message 에러 메시지
     * @return ApiResponse 객체
     */
    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .message(message)
                .data(null)
                .build();
    }
}