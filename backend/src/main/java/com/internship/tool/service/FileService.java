package com.internship.tool.service;

import com.internship.tool.entity.FileEntity;
import com.internship.tool.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {

    // Get file by ID
    public File getFile(Long id) {

       FileEntity entity = fileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("File not found"));

         return new File(entity.getFilePath());
}

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private FileRepository fileRepository;

    // Upload file
    public FileEntity uploadFile(MultipartFile file) throws IOException {

        // Validate size (<10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");                
        }

        // Validate type (allow png, jpg, pdf)
        String contentType = file.getContentType();
        if (!(contentType.equals("image/png") ||
              contentType.equals("image/jpeg") ||
              contentType.equals("application/pdf"))) {
              throw new IllegalArgumentException("Invalid file type");
        }

        // Generate UUID filename
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // Save file to uploads folder
        File dest = new File(UPLOAD_DIR + fileName);
        file.transferTo(dest);

        // Save metadata in DB
        FileEntity entity = new FileEntity();
        entity.setOriginalName(file.getOriginalFilename());
        entity.setFileName(fileName);
        entity.setFilePath(dest.getAbsolutePath());
        entity.setFileType(contentType);
        entity.setSize(file.getSize());

        return fileRepository.save(entity);
    }
}