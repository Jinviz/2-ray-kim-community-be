package jinviz.share_depot_be.dto;

import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostDTOs {

    // 게시글 생성 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostCreateRequest {

        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String content;

        private String thumbnailImage;

        // Entity 변환 메서드
        public Post toEntity(User user) {
            return Post.builder()
                    .title(this.title)
                    .content(this.content)
                    .thumbnailImage(this.thumbnailImage)
                    .views(0)
                    .user(user)
                    .build();
        }
    }

    // 게시글 수정 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostUpdateRequest {

        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력 항목입니다.")
        private String content;

        private String thumbnailImage;
    }

    // 게시글 요약 응답 DTO (목록 조회용)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostSummaryResponse {
        private Integer id;
        private String title;
        private UserDTOs.UserInfoResponse author;
        private String date;
        private int likes;
        private int comments;
        private int views;

        // Entity에서 DTO로 변환하는 정적 메서드
        public static PostSummaryResponse fromEntity(Post post) {
            return PostSummaryResponse.builder()
                    .id(post.getPostId())
                    .title(post.getTitle())
                    .author(UserDTOs.UserInfoResponse.fromEntity(post.getUser()))
                    .date(post.getCreatedAt().toLocalDate().toString())
                    .likes(post.getLikeCount())
                    .comments(post.getCommentCount())
                    .views(post.getViews())
                    .build();
        }
    }

    // 게시글 상세 응답 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetailResponse {
        private Integer id;
        private String title;
        private String content;
        private UserDTOs.UserInfoResponse author;
        private String date;
        private int likes;
        private int comments;
        private int views;
        private List<CommentDTOs.CommentResponse> commentsData;

        // Entity에서 DTO로 변환하는 정적 메서드
        public static PostDetailResponse fromEntity(Post post) {
            List<CommentDTOs.CommentResponse> commentResponses = post.getComments().stream()
                    .map(CommentDTOs.CommentResponse::fromEntity)
                    .collect(Collectors.toList());

            return PostDetailResponse.builder()
                    .id(post.getPostId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .author(UserDTOs.UserInfoResponse.fromEntity(post.getUser()))
                    .date(post.getCreatedAt().toLocalDate().toString())
                    .likes(post.getLikeCount())
                    .comments(post.getCommentCount())
                    .views(post.getViews())
                    .commentsData(commentResponses)
                    .build();
        }
    }

    // 게시글 목록 페이징 응답 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostListResponse {
        private List<PostSummaryResponse> posts;
        private int totalCount;
        private int totalPages;
        private int currentPage;
    }
}