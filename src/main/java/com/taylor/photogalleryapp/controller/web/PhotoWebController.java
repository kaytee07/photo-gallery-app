package com.taylor.photogalleryapp.controller.web;

import com.taylor.photogalleryapp.dto.ApiResponse;
import com.taylor.photogalleryapp.dto.ImageDto;
import com.taylor.photogalleryapp.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class PhotoWebController {
    @Autowired
    private PhotoService photoService;

    @GetMapping("/")
    public String index(Model model) {
        List<ImageDto> images = photoService.getAllImages();
        model.addAttribute("images", images);
        return "index";
    }

    @PostMapping("/upload")
    @ResponseBody
    public ApiResponse<ImageDto> upload(@RequestParam("file") MultipartFile file,
                                        @RequestParam("description") String description) {
        try {
            ImageDto imageDto = photoService.uploadImage(file, description);
            return ApiResponse.success("Image uploaded successfully!", imageDto);
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }
}
