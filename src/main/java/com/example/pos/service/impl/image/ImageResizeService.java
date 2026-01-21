package com.example.pos.service.impl.image;

import jakarta.enterprise.context.ApplicationScoped;
import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@ApplicationScoped
public class ImageResizeService {

    public byte[] resize(byte[] original, int width, int height) {
        try (
                ByteArrayInputStream in = new ByteArrayInputStream(original);
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            Thumbnails.of(in)
                    .size(width, height)
                    .outputFormat("jpg") // 🔥 JPG
                    .outputQuality(0.85f)
                    .toOutputStream(out);

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Image resize failed", e);
        }
    }
}


