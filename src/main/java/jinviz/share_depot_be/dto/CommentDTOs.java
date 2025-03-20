package jinviz.share_depot_be.dto;

import jinviz.share_depot_be.entity.Comment;
import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommentDTOs {

    // 댓글 생성 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentCreateRequest {

        @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
        private String content;

        // Entity 변환 메서드
        public Comment toEntity(User user, Post post) {
            return Comment.builder()
                    .content(this.content)
                    .user(user)
                    .post(post)
                    .build();
        }
    }

    // 댓글 수정 요청 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentUpdateRequest {

        @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
        private String content;
    }

    // 댓글 응답 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private Integer id;
        private String content;
        private UserDTOs.UserInfoResponse author;
        private String date;

        // Entity에서 DTO로 변환하는 정적 메서드
        public static CommentResponse fromEntity(Comment comment) {
            return CommentResponse.builder()
                    .id(comment.getCommentId())
                    .content(comment.getContent())
                    .author(UserDTOs.UserInfoResponse.fromEntity(comment.getUser()))
                    .date(comment.getCreatedAt().toLocalDate().toString())
                    .build();
        }
    }
}