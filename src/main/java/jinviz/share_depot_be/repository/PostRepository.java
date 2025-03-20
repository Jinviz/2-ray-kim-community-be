package jinviz.share_depot_be.repository;

import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    /**
     * 사용자별 게시글 조회
     * @param user 사용자
     * @return 게시글 목록
     */
    List<Post> findByUser(User user);

    /**
     * 사용자별 게시글 페이징 조회
     * @param user 사용자
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 목록
     */
    Page<Post> findByUser(User user, Pageable pageable);

    /**
     * 제목 검색 (페이징)
     * @param title 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 목록
     */
    Page<Post> findByTitleContaining(String title, Pageable pageable);

    /**
     * 내용 검색 (페이징)
     * @param content 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 목록
     */
    Page<Post> findByContentContaining(String content, Pageable pageable);

    /**
     * 제목 또는 내용 검색 (페이징)
     * @param title 제목 검색 키워드
     * @param content 내용 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 목록
     */
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    /**
     * 조회수 증가
     * @param postId 게시글 ID
     */
    @Modifying
    @Query("UPDATE Post p SET p.views = p.views + 1 WHERE p.postId = :postId")
    void incrementViews(@Param("postId") Integer postId);

    /**
     * 인기 게시글 조회 (조회수 기준, 상위 N개)
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 목록
     */
    Page<Post> findAllByOrderByViewsDesc(Pageable pageable);

    /**
     * 최신 게시글 조회 (생성일 기준, 내림차순)
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 목록
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
}