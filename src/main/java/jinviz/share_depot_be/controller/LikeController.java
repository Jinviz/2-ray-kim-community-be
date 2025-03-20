package jinviz.share_depot_be.controller;

import jinviz.share_depot_be.dto.ApiResponse;
import jinviz.share_depot_be.dto.LikeDTOs;
import jinviz.share_depot_be.entity.Like;
import jinviz.share_depot_be.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 좋아요 추가/취소 토글 API
     * @param postId 게시글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 좋아요 응답 DTO
     */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<LikeDTOs.LikeResponse>> toggleLike(
            @PathVariable Integer postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        LikeDTOs.LikeResponse likeResponse = likeService.toggleLike(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(likeResponse));
    }

    /**
     * 좋아요 상태 확인 API
     * @param postId 게시글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 좋아요 상태 응답 DTO
     */
    @GetMapping("/posts/{postId}/status")
    public ResponseEntity<ApiResponse<LikeDTOs.LikeStatusResponse>> getLikeStatus(
            @PathVariable Integer postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        LikeDTOs.LikeStatusResponse likeStatus = likeService.getLikeStatus(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(likeStatus));
    }

    /**
     * 게시글 좋아요 수 조회 API
     * @param postId 게시글 ID
     * @return 좋아요 수
     */
    @GetMapping("/posts/{postId}/count")
    public ResponseEntity<ApiResponse<Integer>> getLikesCount(@PathVariable Integer postId) {
        int likesCount = likeService.getLikesCount(postId);
        return ResponseEntity.ok(ApiResponse.success(likesCount));
    }

    /**
     * 사용자가 좋아요 한 게시글 목록 조회 API
     * @param userId 사용자 ID
     * @return 좋아요 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Iterable<Like>>> getUserLikes(@PathVariable Integer userId) {
        Iterable<Like> userLikes = likeService.getUserLikes(userId);
        return ResponseEntity.ok(ApiResponse.success(userLikes));
    }
}