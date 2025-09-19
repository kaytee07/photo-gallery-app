package com.taylor.photogalleryapp.controller.api;

import com.taylor.photogalleryapp.dto.ApiResponse;
import com.taylor.photogalleryapp.dto.ImageDto;
import com.taylor.photogalleryapp.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class PhotoApiController {
    @Autowired
    private PhotoService photoService;

    @GetMapping
    public ApiResponse<List<ImageDto>> getImages() {
        List<ImageDto> images = photoService.getAllImages();
        return ApiResponse.success("Images fetched successfully", images);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteImage(@PathVariable Long id) {
        try {
            photoService.deleteImage(id);
            return ApiResponse.success("Image deleted successfully", null);
        } catch (Exception e) {
            return ApiResponse.error("Failed to delete image: " + e.getMessage());
        }
    }
}
