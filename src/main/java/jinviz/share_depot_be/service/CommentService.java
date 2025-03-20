package jinviz.share_depot_be.service;

import jinviz.share_depot_be.dto.CommentDTOs;
import jinviz.share_depot_be.entity.Comment;
import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import jinviz.share_depot_be.exception.CustomException;
import jinviz.share_depot_be.exception.ErrorCode;
import jinviz.share_depot_be.repository.CommentRepository;
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
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    /**
     * 댓글 생성
     * @param postId 게시글 ID
     * @param email 현재 로그인한 사용자 이메일
     * @param request 댓글 생성 요청 DTO
     * @return 생성된 댓글 응답 DTO
     */
    @Transactional
    public CommentDTOs.CommentResponse createComment(Integer postId, String email, CommentDTOs.CommentCreateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = request.toEntity(user, post);
        Comment savedComment = commentRepository.save(comment);

        return CommentDTOs.CommentResponse.fromEntity(savedComment);
    }

    /**
     * 게시글별 댓글 조회
     * @param postId 게시글 ID
     * @return 댓글 응답 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<CommentDTOs.CommentResponse> getCommentsByPost(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return commentRepository.findByPostOrderByCreatedAtAsc(post).stream()
                .map(CommentDTOs.CommentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     * @param commentId 댓글 ID
     * @param email 현재 로그인한 사용자 이메일
     * @param request 댓글 수정 요청 DTO
     * @return 수정된 댓글 응답 DTO
     */
    @Transactional
    public CommentDTOs.CommentResponse updateComment(Integer commentId, String email, CommentDTOs.CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 작성자 확인
        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        // 댓글 내용 업데이트
        comment.setContent(request.getContent());
        Comment updatedComment = commentRepository.save(comment);

        return CommentDTOs.CommentResponse.fromEntity(updatedComment);
    }

    /**
     * 댓글 삭제
     * @param commentId 댓글 ID
     * @param email 현재 로그인한 사용자 이메일
     */
    @Transactional
    public void deleteComment(Integer commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 작성자 확인
        if (!comment.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACTION);
        }

        commentRepository.delete(comment);
    }

    /**
     * 사용자별 댓글 조회
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 댓글 응답 DTO 페이지
     */
    @Transactional(readOnly = true)
    public Page<CommentDTOs.CommentResponse> getUserComments(Integer userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Comment> comments = commentRepository.findByUser(user, pageable);

        return comments.map(CommentDTOs.CommentResponse::fromEntity);
    }
}