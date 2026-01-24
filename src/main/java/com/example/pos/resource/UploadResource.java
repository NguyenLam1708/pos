package com.example.pos.resource;

import com.example.pos.dto.request.ImageUploadForm;
import com.example.pos.service.impl.image.FileStorageService;
import com.example.pos.service.impl.image.ImageResizeService;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;

@Path("/api/v1/uploads")
@ApplicationScoped
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
@Blocking
public class UploadResource {

    @Inject
    FileStorageService fileStorageService;

    @Inject
    ImageResizeService imageResizeService;

    @POST
    public Response upload(@BeanParam ImageUploadForm form) {

        if (form.file == null) {
            throw new WebApplicationException("File is required", 400);
        }

        try {
            byte[] bytes = Files.readAllBytes(form.file.uploadedFile());

            byte[] resized = imageResizeService.resizeToJpeg(
                    bytes, 1024, 1024
            );

            String publicId = "products/" + UUID.randomUUID();

            String imageUrl = fileStorageService.uploadBlocking(
                    resized,
                    publicId
            );

            Files.deleteIfExists(form.file.uploadedFile());

            return Response.ok(
                    Map.of("imageUrl", imageUrl)
            ).build();

        } catch (Exception e) {
            throw new RuntimeException("Upload image failed", e);
        }
    }
}
