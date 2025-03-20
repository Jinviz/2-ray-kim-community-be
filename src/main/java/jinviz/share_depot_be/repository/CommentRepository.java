package jinviz.share_depot_be.repository;

import jinviz.share_depot_be.entity.Comment;
import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    /**
     * 게시글별 댓글 조회
     * @param post 게시글
     * @return 댓글 목록
     */
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);

    /**
     * 게시글별 댓글 페이징 조회
     * @param post 게시글
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     */
    Page<Comment> findByPost(Post post, Pageable pageable);

    /**
     * 사용자별 댓글 조회
     * @param user 사용자
     * @return 댓글 목록
     */
    List<Comment> findByUser(User user);

    /**
     * 사용자별 댓글 페이징 조회
     * @param user 사용자
     * @param pageable 페이징 정보
     * @return 페이징된 댓글 목록
     */
    Page<Comment> findByUser(User user, Pageable pageable);

    /**
     * 게시글별 댓글 수 조회
     * @param post 게시글
     * @return 댓글 수
     */
    long countByPost(Post post);

    /**
     * 게시글 ID로 댓글 삭제
     * @param postId 게시글 ID
     */
    void deleteByPostPostId(Integer postId);

    /**
     * 사용자 ID로 댓글 삭제
     * @param userId 사용자 ID
     */
    void deleteByUserUserId(Integer userId);
}