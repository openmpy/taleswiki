package com.openmpy.taleswiki.common.presentation;

import com.openmpy.taleswiki.common.application.ImageService;
import com.openmpy.taleswiki.common.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/api/v1/images/upload")
    public ResponseEntity<ImageUploadResponse> upload(@RequestParam("image") final MultipartFile file) {
        final ImageUploadResponse response = imageService.upload(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/images/tmp/{fileName}")
    public Resource getImageTmpResource(@PathVariable final String fileName) {
        return imageService.getImageTmpResource(fileName);
    }

    @GetMapping("/images/{fileName}")
    public Resource getImageResource(@PathVariable final String fileName) {
        return imageService.getImageResource(fileName);
    }
}
