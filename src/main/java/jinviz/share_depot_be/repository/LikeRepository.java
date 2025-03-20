package jinviz.share_depot_be.repository;

import jinviz.share_depot_be.entity.Like;
import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Integer> {

    /**
     * 게시글과 사용자로 좋아요 조회
     * @param post 게시글
     * @param user 사용자
     * @return 좋아요 Optional
     */
    Optional<Like> findByPostAndUser(Post post, User user);

    /**
     * 게시글별 좋아요 수 조회
     * @param post 게시글
     * @return 좋아요 수
     */
    long countByPost(Post post);

    /**
     * 게시글과 사용자로 좋아요 존재 여부 확인
     * @param post 게시글
     * @param user 사용자
     * @return 존재 여부
     */
    boolean existsByPostAndUser(Post post, User user);

    /**
     * 게시글 ID로 좋아요 삭제
     * @param postId 게시글 ID
     */
    void deleteByPostPostId(Integer postId);

    /**
     * 사용자 ID로 좋아요 삭제
     * @param userId 사용자 ID
     */
    void deleteByUserUserId(Integer userId);

    /**
     * 사용자별 좋아요한 게시글 목록 조회
     * @param user 사용자
     * @return 좋아요 목록
     */
    Iterable<Like> findByUser(User user);
}