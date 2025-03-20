package jinviz.share_depot_be.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 오류가 발생했습니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "잘못된 타입의 값입니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근이 거부되었습니다."),

    // 사용자 관련 에러
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U001", "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "이미 존재하는 닉네임입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U003", "사용자를 찾을 수 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "U004", "잘못된 인증 정보입니다."),
    UNAUTHORIZED_ACTION(HttpStatus.FORBIDDEN, "U005", "권한이 없는 작업입니다."),
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "U006", "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_CONFIRM_MISMATCH(HttpStatus.BAD_REQUEST, "U007", "새 비밀번호와 확인 비밀번호가 일치하지 않습니다."),

    // 게시글 관련 에러
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "게시글을 찾을 수 없습니다."),
    POST_TITLE_EMPTY(HttpStatus.BAD_REQUEST, "P002", "게시글 제목을 입력해주세요."),
    POST_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "P003", "게시글 내용을 입력해주세요."),

    // 댓글 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CM001", "댓글을 찾을 수 없습니다."),
    COMMENT_CONTENT_EMPTY(HttpStatus.BAD_REQUEST, "CM002", "댓글 내용을 입력해주세요."),

    // 파일 관련 에러
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F001", "파일 업로드 중 오류가 발생했습니다."),
    FILE_DOWNLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F002", "파일 다운로드 중 오류가 발생했습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "F003", "파일을 찾을 수 없습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F004", "파일 삭제 중 오류가 발생했습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "F005", "지원하지 않는 파일 형식입니다."),

    // 인증 관련 에러
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A001", "인증 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "A002", "잘못된 인증 토큰입니다."),
    JWT_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "A003", "인증 토큰이 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}