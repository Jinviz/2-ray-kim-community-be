package jinviz.share_depot_be.service;

import jinviz.share_depot_be.dto.FileDTO;
import jinviz.share_depot_be.exception.CustomException;
import jinviz.share_depot_be.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload.directory}")
    private String uploadDir;

    /**
     * 파일 업로드
     * @param file 업로드할 파일
     * @param subDirectory 하위 디렉토리 (프로필 이미지, 게시글 이미지 등)
     * @return 파일 정보 DTO
     */
    public FileDTO uploadFile(MultipartFile file, String subDirectory) {
        try {
            // 원본 파일명 추출
            String originalFilename = file.getOriginalFilename();

            // 파일 확장자 추출
            String extension = FilenameUtils.getExtension(originalFilename);

            // 저장할 파일명 생성 (UUID + 확장자)
            String storedFilename = UUID.randomUUID() + "." + extension;

            // 파일 저장 경로 생성
            Path uploadPath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();

            // 디렉토리가 없으면 생성
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path targetLocation = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 파일 접근 URL 생성
            String fileUrl = String.format("/%s/%s/%s", uploadDir, subDirectory, storedFilename);

            // 파일 정보 DTO 반환
            return FileDTO.builder()
                    .originalFilename(originalFilename)
                    .storedFilename(storedFilename)
                    .fileUrl(fileUrl)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    /**
     * 파일 다운로드
     * @param subDirectory 하위 디렉토리
     * @param filename 파일명
     * @return 파일 리소스
     */
    public Resource downloadFile(String subDirectory, String filename) {
        try {
            Path filePath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize().resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    /**
     * 파일 삭제
     * @param subDirectory 하위 디렉토리
     * @param filename 파일명
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(String subDirectory, String filename) {
        try {
            Path filePath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize().resolve(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_DELETE_ERROR);
        }
    }
}