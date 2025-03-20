package jinviz.share_depot_be.service;

import jinviz.share_depot_be.dto.UserDTOs;
import jinviz.share_depot_be.entity.User;
import jinviz.share_depot_be.repository.UserRepository;
import jinviz.share_depot_be.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * 회원가입
     * @param request 회원가입 요청 DTO
     * @return 생성된 사용자 ID
     */
    @Transactional
    public Integer signup(UserDTOs.SignupRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 엔티티 생성 및 저장
        User user = request.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        return savedUser.getUserId();
    }

    /**
     * 로그인
     * @param request 로그인 요청 DTO
     * @return 로그인 응답 DTO (토큰 및 사용자 정보)
     */
    @Transactional(readOnly = true)
    public UserDTOs.LoginResponse login(UserDTOs.LoginRequest request) {
        // Spring Security를 통한 인증
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 사용자 정보 조회
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            // JWT 토큰 생성
            String token = jwtTokenProvider.createToken(user.getEmail(), user.getUserId());

            // 로그인 응답 DTO 생성
            return UserDTOs.LoginResponse.builder()
                    .token(token)
                    .user(UserDTOs.UserInfoResponse.fromEntity(user))
                    .build();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }
    }
}