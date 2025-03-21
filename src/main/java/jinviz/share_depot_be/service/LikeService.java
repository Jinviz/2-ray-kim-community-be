package jinviz.share_depot_be.service;

import jinviz.share_depot_be.dto.LikeDTOs;
import jinviz.share_depot_be.entity.Like;
import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import jinviz.share_depot_be.exception.CustomException;
import jinviz.share_depot_be.exception.ErrorCode;
import jinviz.share_depot_be.repository.LikeRepository;
import jinviz.share_depot_be.repository.PostRepository;
import jinviz.share_depot_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 좋아요 추가/취소 토글
     * @param postId 게시글 ID
     * @param email 현재 로그인한 사용자 이메일
     * @return 좋아요 응답 DTO
     */
    @Transactional
    public LikeDTOs.LikeResponse toggleLike(Integer postId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

        // 좋아요가 이미 존재하면 취소, 아니면 추가
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return LikeDTOs.LikeResponse.builder()
                    .postId(postId)
                    .likesCount((int) likeRepository.countByPost(post))
                    .userLiked(false)
                    .build();
        } else {
            Like like = Like.builder()
                    .post(post)
                    .user(user)
                    .build();
            likeRepository.save(like);
            return LikeDTOs.LikeResponse.builder()
                    .postId(postId)
                    .likesCount((int) likeRepository.countByPost(post))
                    .userLiked(true)
                    .build();
        }
    }
}