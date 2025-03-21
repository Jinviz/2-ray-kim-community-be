package jinviz.share_depot_be.controller;

import jinviz.share_depot_be.dto.ApiResponse;
import jinviz.share_depot_be.dto.LikeDTOs;
import jinviz.share_depot_be.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /**
     * 좋아요 추가/취소 토글 API
     * @param postId 게시글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 좋아요 응답 DTO
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeDTOs.LikeResponse>> toggleLike(
            @PathVariable Integer postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        LikeDTOs.LikeResponse likeResponse = likeService.toggleLike(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(likeResponse));
    }
}