package jinviz.share_depot_be.controller;

import jinviz.share_depot_be.dto.ApiResponse;
import jinviz.share_depot_be.dto.CommentDTOs;
import jinviz.share_depot_be.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 API
     * @param postId 게시글 ID
     * @param userDetails 인증된 사용자 정보
     * @param request 댓글 생성 요청 DTO
     * @return 생성된 댓글 응답 DTO
     */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<CommentDTOs.CommentResponse>> createComment(
            @PathVariable Integer postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CommentDTOs.CommentCreateRequest request) {
        CommentDTOs.CommentResponse comment = commentService.createComment(postId, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(comment, "comment_created"));
    }

    /**
     * 게시글별 댓글 목록 조회 API
     * @param postId 게시글 ID
     * @return 댓글 응답 DTO 목록
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<List<CommentDTOs.CommentResponse>>> getCommentsByPost(@PathVariable Integer postId) {
        List<CommentDTOs.CommentResponse> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    /**
     * 댓글 수정 API
     * @param commentId 댓글 ID
     * @param userDetails 인증된 사용자 정보
     * @param request 댓글 수정 요청 DTO
     * @return 수정된 댓글 응답 DTO
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentDTOs.CommentResponse>> updateComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CommentDTOs.CommentUpdateRequest request) {
        CommentDTOs.CommentResponse updatedComment = commentService.updateComment(commentId, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(updatedComment, "comment_updated"));
    }

    /**
     * 댓글 삭제 API
     * @param commentId 댓글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 응답 메시지
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Integer commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "comment_deleted"));
    }

    /**
     * 사용자별 댓글 목록 조회 API
     * @param userId 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 댓글 응답 DTO 페이지
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<CommentDTOs.CommentResponse>>> getUserComments(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CommentDTOs.CommentResponse> comments = commentService.getUserComments(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }
}