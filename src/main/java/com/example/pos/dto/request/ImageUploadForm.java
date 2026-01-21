package com.example.pos.dto.request;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class ImageUploadForm {

    @RestForm("file")
    public FileUpload file;
}
