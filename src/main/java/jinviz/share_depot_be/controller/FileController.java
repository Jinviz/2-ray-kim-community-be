package jinviz.share_depot_be.controller;

import jinviz.share_depot_be.dto.ApiResponse;
import jinviz.share_depot_be.dto.FileDTO;
import jinviz.share_depot_be.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 허용되는 이미지 파일 확장자
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    /**
     * 프로필 이미지 업로드 API
     * @param file 업로드할 이미지 파일
     * @return 파일 정보 DTO
     */
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<FileDTO>> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        validateImageFile(file);
        FileDTO fileDTO = fileService.uploadFile(file, "profiles");
        return ResponseEntity.ok(ApiResponse.success(fileDTO));
    }

    /**
     * 게시글 이미지 업로드 API
     * @param file 업로드할 이미지 파일
     * @return 파일 정보 DTO
     */
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<FileDTO>> uploadPostImage(@RequestParam("file") MultipartFile file) {
        validateImageFile(file);
        FileDTO fileDTO = fileService.uploadFile(file, "posts");
        return ResponseEntity.ok(ApiResponse.success(fileDTO));
    }

    /**
     * 파일 다운로드 API
     * @param subDirectory 하위 디렉토리
     * @param filename 파일명
     * @return 파일 리소스
     */
    @GetMapping("/{subDirectory}/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String subDirectory,
            @PathVariable String filename) {
        Resource resource = fileService.downloadFile(subDirectory, filename);

        // 콘텐츠 타입 추측
        String contentType = determineContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    /**
     * 파일 삭제 API
     * @param subDirectory 하위 디렉토리
     * @param filename 파일명
     * @return 삭제 성공 여부
     */
    @DeleteMapping("/{subDirectory}/{filename:.+}")
    public ResponseEntity<ApiResponse<Boolean>> deleteFile(
            @PathVariable String subDirectory,
            @PathVariable String filename) {
        boolean deleted = fileService.deleteFile(subDirectory, filename);
        return ResponseEntity.ok(ApiResponse.success(deleted, "file_deleted"));
    }

    /**
     * 이미지 파일 유효성 검사
     * @param file 업로드할 파일
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("파일명이 없습니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. 허용되는 형식: " + String.join(", ", ALLOWED_IMAGE_EXTENSIONS));
        }
    }

    /**
     * 파일 확장자에 따른 컨텐츠 타입 결정
     * @param filename 파일명
     * @return 컨텐츠 타입
     */
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }
}