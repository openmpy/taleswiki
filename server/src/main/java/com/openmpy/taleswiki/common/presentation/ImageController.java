package com.openmpy.taleswiki.common.presentation;

import com.openmpy.taleswiki.common.application.ImageS3Service;
import com.openmpy.taleswiki.common.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageS3Service imageS3Service;

    @PostMapping("/api/v1/images/upload")
    public ResponseEntity<ImageUploadResponse> upload(@RequestParam("image") final MultipartFile file) {
        final ImageUploadResponse response = imageS3Service.upload(file);
        return ResponseEntity.ok(response);
    }
}
