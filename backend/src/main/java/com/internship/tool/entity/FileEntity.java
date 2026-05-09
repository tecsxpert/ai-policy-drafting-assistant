package com.internship.tool.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // unique ID

    private String originalName;  // original file name
    private String fileName;      // stored UUID file name
    private String filePath;      // file location in system
    private String fileType;      // MIME type (image/pdf)
    private long size;            // file size
}