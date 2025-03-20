package jinviz.share_depot_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 파일 업로드 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {

    private String originalFilename;
    private String storedFilename;
    private String fileUrl;
    private String fileType;
    private long fileSize;
}