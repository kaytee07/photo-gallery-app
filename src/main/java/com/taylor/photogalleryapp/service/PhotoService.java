package com.taylor.photogalleryapp.service;

import com.taylor.photogalleryapp.dto.ImageDto;
import com.taylor.photogalleryapp.entity.ImageMetadata;
import com.taylor.photogalleryapp.repository.ImageMetadataRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PhotoService {
    private final ImageMetadataRepository repository;
    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucketName;
    private final Validator validator;

    @Autowired
    public PhotoService(ImageMetadataRepository repository, @Value("${aws.s3.bucket}") String bucketName, Validator validator) {
        this.repository = repository;
        this.bucketName = bucketName;
        this.validator = validator;
        this.s3Client = S3Client.builder()
                .region(Region.of(System.getenv("AWS_REGION") != null ? System.getenv("AWS_REGION") : "us-east-1"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.presigner = S3Presigner.builder()
                .region(Region.of(System.getenv("AWS_REGION") != null ? System.getenv("AWS_REGION") : "us-east-1"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public ImageDto uploadImage(MultipartFile file, String description) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        String objectKey = "images/" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(objectKey).build(),
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(2))
                .getObjectRequest(software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                        .bucket(bucketName).key(objectKey).build())
                .build();
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();

        ImageMetadata metadata = new ImageMetadata();
        metadata.setS3ObjectKey(objectKey);
        metadata.setDescription(description);
        metadata.setPresignedUrl(presignedUrl);
        repository.save(metadata);

        return new ImageDto(description, presignedUrl, metadata.getId());
    }

    public void deleteImage(Long id) {
        ImageMetadata image = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id:: " + id));


        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(image.getS3ObjectKey())
                .build());

        repository.deleteById(id);
    }

    public List<ImageDto> getAllImages() {
        return repository.findAll().stream()
                .map(img -> {
                    ImageDto dto = new ImageDto(img.getDescription(), img.getPresignedUrl(), img.getId());
                    Set<jakarta.validation.ConstraintViolation<ImageDto>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        throw new IllegalStateException("Invalid ImageDto: " + violations);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}