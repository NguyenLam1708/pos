package com.example.pos.service.impl.image;

import io.quarkus.arc.profile.IfBuildProfile;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
@IfBuildProfile("dev")
public class LocalStorageService implements FileStorageService {

    private static final Path ROOT = Path.of("uploads");

    @Override
    public Uni<String> upload(byte[] data, String path, String contentType) {

        return Uni.createFrom().item(() -> {
            try {
                Path filePath = ROOT.resolve(path);

                // tạo folder nếu chưa có
                Files.createDirectories(filePath.getParent());

                // ghi file
                Files.write(filePath, data);

                return "http://localhost:8080/uploads/" + path;

            } catch (Exception e) {
                throw new RuntimeException("Local file upload failed", e);
            }
        });
    }
}
