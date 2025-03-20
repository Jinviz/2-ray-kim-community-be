package jinviz.share_depot_be.service;

import jinviz.share_depot_be.dto.UserDTOs;
import jinviz.share_depot_be.entity.User;
import jinviz.share_depot_be.repository.CommentRepository;
import jinviz.share_depot_be.repository.LikeRepository;
import jinviz.share_depot_be.repository.PostRepository;
import jinviz.share_depot_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 정보 조회
     * @param userId 사용자 ID
     * @return 사용자 정보 응답 DTO
     */
    @Transactional(readOnly = true)
    public UserDTOs.UserInfoResponse getUserInfo(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserDTOs.UserInfoResponse.fromEntity(user);
    }

    /**
     * 현재 로그인한 사용자 정보 조회
     * @param email 이메일
     * @return 사용자 정보 응답 DTO
     */
    @Transactional(readOnly = true)
    public UserDTOs.UserInfoResponse getMyInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserDTOs.UserInfoResponse.fromEntity(user);
    }

    /**
     * 사용자 정보 업데이트
     * @param email 현재 로그인한 사용자 이메일
     * @param request 사용자 정보 수정 요청 DTO
     * @return 업데이트된 사용자 정보 응답 DTO
     */
    @Transactional
    public UserDTOs.UserInfoResponse updateUserInfo(String email, UserDTOs.UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 닉네임 변경 시 중복 확인
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
            }
            user.setNickname(request.getNickname());
        }

        // 프로필 이미지 변경
        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage());
        }

        User updatedUser = userRepository.save(user);
        return UserDTOs.UserInfoResponse.fromEntity(updatedUser);
    }

    /**
     * 비밀번호 변경
     * @param email 현재 로그인한 사용자 이메일
     * @param request 비밀번호 변경 요청 DTO
     */
    @Transactional
    public void changePassword(String email, UserDTOs.PasswordChangeRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        // 새 비밀번호와 확인 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        // 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 회원 탈퇴
     * @param email 현재 로그인한 사용자 이메일
     */
    @Transactional
    public void withdrawUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관련 데이터 삭제
        likeRepository.deleteByUserUserId(user.getUserId());
        commentRepository.deleteByUserUserId(user.getUserId());
        // 게시글은 개별 처리 필요 (첨부 파일 등이 있을 수 있음)
        user.getPosts().forEach(post -> {
            likeRepository.deleteByPostPostId(post.getPostId());
            commentRepository.deleteByPostPostId(post.getPostId());
        });
        postRepository.deleteAll(user.getPosts());

        // 사용자 삭제
        userRepository.delete(user);
    }
}