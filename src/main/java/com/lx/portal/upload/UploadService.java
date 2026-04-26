package com.lx.portal.upload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 3 * 1024 * 1024;

    private final Path uploadDir;
    private final UploadFileRepository repository;

    public UploadService(@Value("${app.upload.dir:uploads}") String uploadDir, UploadFileRepository repository) {
        this.uploadDir = Path.of(uploadDir);
        this.repository = repository;
    }

    @Transactional
    public UploadFile upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("上传文件不能超过 3MB");
        }
        String contentType = file.getContentType() == null ? "" : file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("仅支持 jpg、png、webp 图片");
        }
        Files.createDirectories(uploadDir);
        String extension = switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
        String storedName = UUID.randomUUID() + extension;
        Path target = uploadDir.resolve(storedName);
        file.transferTo(target);

        UploadFile uploadFile = new UploadFile();
        uploadFile.setOriginalName(file.getOriginalFilename() == null ? storedName : file.getOriginalFilename());
        uploadFile.setStoredName(storedName);
        uploadFile.setContentType(contentType);
        uploadFile.setSizeBytes(file.getSize());
        uploadFile.setPublicUrl("/uploads/" + storedName);
        return repository.save(uploadFile);
    }
}

