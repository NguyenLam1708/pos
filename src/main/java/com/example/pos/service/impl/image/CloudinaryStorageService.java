package com.example.pos.service.impl.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Map;

@ApplicationScoped
@IfBuildProfile("prod")
public class CloudinaryStorageService implements FileStorageService {

    @ConfigProperty(name = "cloudinary.cloud-name")
    String cloudName;

    @ConfigProperty(name = "cloudinary.api-key")
    String apiKey;

    @ConfigProperty(name = "cloudinary.api-secret")
    String apiSecret;

    @ConfigProperty(name = "cloudinary.folder")
    String baseFolder;

    Cloudinary cloudinary;

    @PostConstruct
    void init() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @Override
    public Uni<String> upload(byte[] data, String path, String contentType) {
        return Uni.createFrom().item(() -> {

            if (data == null || data.length == 0) {
                throw new IllegalArgumentException("Image data is empty");
            }

            String publicId = path
                    .replaceAll(".*/", "")
                    .replaceAll("\\.(png|jpg|jpeg|webp)$", "");

            try {
                Map<?, ?> result = cloudinary.uploader().upload(
                        data,
                        ObjectUtils.asMap(
                                "folder", baseFolder,
                                "public_id", publicId,
                                "overwrite", true
                        )
                );

                return result.get("secure_url").toString();

            } catch (Exception e) {
                throw new RuntimeException("Cloudinary upload failed", e);
            }

        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

    @Override
    public Uni<Void> delete(String publicId) {
        return Uni.createFrom().item(publicId)
                .invoke(id -> {
                    try {
                        cloudinary.uploader().destroy(
                                id,
                                ObjectUtils.asMap("resource_type", "image")
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Cloudinary delete failed", e);
                    }
                })
                .replaceWithVoid()
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }

}
