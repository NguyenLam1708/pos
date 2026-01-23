package com.example.pos.service.impl.image;

public interface FileStorageService {

    String uploadBlocking(byte[] data, String publicId);
    void deleteBlocking(String fullPublicId);
}
