package jinviz.share_depot_be.service;

import jinviz.share_depot_be.dto.PostDTOs;
import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import jinviz.share_depot_be.exception.CustomException;
import jinviz.share_depot_be.exception.ErrorCode;
import jinviz.share_depot_be.repository.CommentRepository;
import jinviz.share_depot_be.repository.LikeRepository;
import jinviz.share_depot_be.repository.PostRepository;
import jinviz.share_depot_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    /**
     * 게시글 목록 조회 (페이징)
     * @param pageable 페이징 정보
     * @return 게시글 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public PostDTOs.PostListResponse getPosts(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<PostDTOs.PostSummaryResponse> postSummaries = postsPage.getContent().stream()
                .map(PostDTOs.PostSummaryResponse::fromEntity)
                .collect(Collectors.toList());

        return PostDTOs.PostListResponse.builder()
                .posts(postSummaries)
                .totalCount((int) postsPage.getTotalElements())
                .totalPages(postsPage.getTotalPages())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * 인기 게시글 목록 조회 (조회수 기준)
     * @param pageable 페이징 정보
     * @return 게시글 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public PostDTOs.PostListResponse getPopularPosts(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAllByOrderByViewsDesc(pageable);

        List<PostDTOs.PostSummaryResponse> postSummaries = postsPage.getContent().stream()
                .map(PostDTOs.PostSummaryResponse::fromEntity)
                .collect(Collectors.toList());

        return PostDTOs.PostListResponse.builder()
                .posts(postSummaries)
                .totalCount((int) postsPage.getTotalElements())
                .totalPages(postsPage.getTotalPages())
                .currentPage(pageable.getPageNumber() + 1)
                .build();
    }

    /**
     * 게시글 상세 조회
     * @param postId 게시글 ID
     * @return 게시글 상세 응답 DTO
     */
    @Transactional
    public PostDTOs.PostDetailResponse getPostDetail(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // 조회수 증가
        postRepository.incrementViews(postId);
        post.setViews(post.getViews() + 1);

        return PostDTOs.PostDetailResponse.fromEntity(post);
    }

    /**
     * 게시글 작성
     * @param email 현재 로그인한 사용자 이메일
     * @param request 게시글 생성 요청 DTO
     * @return 생성된 게시글 ID
     */
    @Transactional
    public Integer createPost(String email, PostDTOs.PostCreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = request.toEntity(user);
        Post savedPost = postRepository.save(post);

        return savedPost.getPostId();
    }

    /**
     * 게시글 수정
     * @param postId 게시글 ID
     * @param email 현재 로그인한 사용자 이메일
     * @param request 게시글 수정 요청 DTO
     * @return 수정된 게시글 상세 응답 DTO
     */
    @Transactional
    public PostDTOs.PostDetailResponse updatePost(Integer postId, String email, PostDTOs.PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 작성자 확인
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        // 게시글 정보 업데이트
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        if (request.getThumbnailImage() != null) {
            post.setThumbnailImage(request.getThumbnailImage());
        }

        Post updatedPost = postRepository.save(post);
        return PostDTOs.PostDetailResponse.fromEntity(updatedPost);
    }

    /**
     * 게시글 삭제
     * @param postId 게시글 ID
     * @param email 현재 로그인한 사용자 이메일
     */
    @Transactional
    public void deletePost(Integer postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 작성자 확인
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        // 관련 데이터 삭제
        likeRepository.deleteByPostPostId(postId);
        commentRepository.deleteByPostPostId(postId);

        // 게시글 삭제
        postRepository.delete(post);
    }
}