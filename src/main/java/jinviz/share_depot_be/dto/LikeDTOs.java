package jinviz.share_depot_be.dto;

import jinviz.share_depot_be.entity.Like;
import jinviz.share_depot_be.entity.Post;
import jinviz.share_depot_be.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class LikeDTOs {

    // 좋아요 응답 DTO
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResponse {
        private Integer postId;
        private Integer likesCount;
        private boolean userLiked;
    }

    // 좋아요 상태 DTO (특정 사용자의 게시글 좋아요 여부)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeStatusResponse {
        private boolean liked;
    }
}