package jinviz.share_depot_be.controller;

import jinviz.share_depot_be.dto.ApiResponse;
import jinviz.share_depot_be.dto.PostDTOs;
import jinviz.share_depot_be.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 목록 조회 API
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 게시글 목록 응답 DTO
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PostDTOs.PostListResponse>> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page -1, size, Sort.by("createdAt").descending());
        PostDTOs.PostListResponse posts = postService.getPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    /**
     * 인기 게시글 목록 조회 API
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 게시글 목록 응답 DTO
     */
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<PostDTOs.PostListResponse>> getPopularPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("views").descending());
        PostDTOs.PostListResponse posts = postService.getPopularPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    /**
     * 게시글 상세 조회 API
     * @param postId 게시글 ID
     * @return 게시글 상세 응답 DTO
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDTOs.PostDetailResponse>> getPostDetail(@PathVariable Integer postId) {
        PostDTOs.PostDetailResponse post = postService.getPostDetail(postId);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    /**
     * 게시글 작성 API
     * @param userDetails 인증된 사용자 정보
     * @param request 게시글 생성 요청 DTO
     * @return 생성된 게시글 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Integer>> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostDTOs.PostCreateRequest request) {
        Integer postId = postService.createPost(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(postId, "post_created"));
    }

    /**
     * 게시글 수정 API
     * @param postId 게시글 ID
     * @param userDetails 인증된 사용자 정보
     * @param request 게시글 수정 요청 DTO
     * @return 수정된 게시글 상세 응답 DTO
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDTOs.PostDetailResponse>> updatePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostDTOs.PostUpdateRequest request) {
        PostDTOs.PostDetailResponse updatedPost = postService.updatePost(postId, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(updatedPost, "post_updated"));
    }

    /**
     * 게시글 삭제 API
     * @param postId 게시글 ID
     * @param userDetails 인증된 사용자 정보
     * @return 응답 메시지
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "post_deleted"));
    }
}