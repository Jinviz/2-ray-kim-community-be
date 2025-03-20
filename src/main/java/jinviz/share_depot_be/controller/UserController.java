package jinviz.share_depot_be.controller;

import jinviz.share_depot_be.dto.ApiResponse;
import jinviz.share_depot_be.dto.UserDTOs;
import jinviz.share_depot_be.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 사용자 정보 조회 API
     * @param userId 사용자 ID
     * @return 사용자 정보 응답 DTO
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDTOs.UserInfoResponse>> getUserInfo(@PathVariable Integer userId) {
        UserDTOs.UserInfoResponse userInfo = userService.getUserInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * 현재 로그인한 사용자 정보 조회 API
     * @param userDetails 인증된 사용자 정보
     * @return 사용자 정보 응답 DTO
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTOs.UserInfoResponse>> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTOs.UserInfoResponse userInfo = userService.getMyInfo(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

    /**
     * 사용자 정보 수정 API
     * @param userDetails 인증된 사용자 정보
     * @param request 사용자 정보 수정 요청 DTO
     * @return 수정된 사용자 정보 응답 DTO
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTOs.UserInfoResponse>> updateUserInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDTOs.UserUpdateRequest request) {
        UserDTOs.UserInfoResponse updatedInfo = userService.updateUserInfo(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(updatedInfo, "user_updated"));
    }

    /**
     * 비밀번호 변경 API
     * @param userDetails 인증된 사용자 정보
     * @param request 비밀번호 변경 요청 DTO
     * @return 응답 메시지
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserDTOs.PasswordChangeRequest request) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(null, "password_changed"));
    }

    /**
     * 회원 탈퇴 API
     * @param userDetails 인증된 사용자 정보
     * @return 응답 메시지
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> withdrawUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.withdrawUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "user_deleted"));
    }
}