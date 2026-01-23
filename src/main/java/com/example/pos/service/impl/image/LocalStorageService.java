package com.example.pos.service.impl.image;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Files;
import java.nio.file.Path;

@ApplicationScoped
@IfBuildProfile("dev")
public class LocalStorageService implements FileStorageService {

    private static final Path ROOT = Path.of("uploads");

    @Override
    public String uploadBlocking(byte[] data, String path) {
        try {
            Path filePath = ROOT.resolve(path);

            Files.createDirectories(filePath.getParent());
            Files.write(filePath, data);

            return "http://localhost:8080/uploads/" + path;

        } catch (Exception e) {
            throw new RuntimeException("Local file upload failed", e);
        }
    }

    @Override
    public void deleteBlocking(String path) {
        try {
            Path filePath = ROOT.resolve(path);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Local file delete failed", e);
        }
    }
}
