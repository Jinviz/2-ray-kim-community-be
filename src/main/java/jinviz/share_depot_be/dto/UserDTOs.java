package jinviz.share_depot_be.dto;

import jinviz.share_depot_be.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserDTOs {

    // 회원가입 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupRequest {

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Size(min = 4, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String password;

        @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자에서 20자 사이여야 합니다.")
        private String nickname;

        private String profileImage;

        // Entity 변환 메서드
        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(this.email)
                    .password(encodedPassword)
                    .nickname(this.nickname)
                    .profileImage(this.profileImage)
                    .build();
        }
    }

    // 로그인 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {

        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        private String password;
    }

    // 로그인 응답 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private UserInfoResponse user;
    }

    // 사용자 정보 응답 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoResponse {
        private Integer userId;
        private String email;
        private String nickname;
        private String profileImage;
        private LocalDateTime createdAt;

        // Entity에서 DTO로 변환하는 정적 메서드
        public static UserInfoResponse fromEntity(User user) {
            return UserInfoResponse.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .profileImage(user.getProfileImage())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }

    // 사용자 정보 수정 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdateRequest {

        @Size(min = 2, max = 20, message = "닉네임은 2자에서 20자 사이여야 합니다.")
        private String nickname;

        private String profileImage;
    }

    // 비밀번호 변경 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswordChangeRequest {

        @NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다.")
        private String currentPassword;

        @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다.")
        @Size(min = 4, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String newPassword;

        @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
        private String confirmPassword;
    }
}