package com.example.pos.service.impl.image;

import io.smallrye.mutiny.Uni;

public interface FileStorageService {

    Uni<String> upload(
            byte[] data,
            String path,
            String contentType
    );
}
