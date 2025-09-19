package com.taylor.photogalleryapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageDto {
    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Presigned URL cannot be empty")
    private String presignedUrl;

    private Long id;

    public ImageDto(String description, String presignedUrl, long id) {
        this.description = description;
        this.presignedUrl = presignedUrl;
        this.id = id;
    }
}