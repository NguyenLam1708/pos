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

    public String uploadBlocking(byte[] data, String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    data,
                    ObjectUtils.asMap(
                            "public_id", baseFolder + "/" + publicId,
                            "overwrite", true,
                            "resource_type", "image"
                    )
            );
            return result.get("secure_url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }

    @Override
    public void deleteBlocking(String fullPublicId) {
        try {
            cloudinary.uploader().destroy(
                    fullPublicId,
                    ObjectUtils.asMap("resource_type", "image")
            );
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary delete failed", e);
        }
    }

}
