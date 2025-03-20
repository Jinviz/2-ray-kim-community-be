package jinviz.share_depot_be.controller;

import jinviz.share_depot_be.dto.ApiResponse;
import jinviz.share_depot_be.dto.UserDTOs;
import jinviz.share_depot_be.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     * @param request 회원가입 요청 DTO
     * @return 사용자 ID
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Integer>> signup(@Valid @RequestBody UserDTOs.SignupRequest request) {
        Integer userId = authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(userId, "register_success"));
    }

    /**
     * 로그인 API
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO (토큰 및 사용자 정보)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDTOs.LoginResponse>> login(@Valid @RequestBody UserDTOs.LoginRequest request) {
        UserDTOs.LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "login_success"));
    }
}