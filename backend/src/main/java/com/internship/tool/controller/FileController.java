package com.internship.tool.controller;

import com.internship.tool.entity.FileEntity;
import com.internship.tool.service.FileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.InputStreamResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController // Marks this class as REST Controller
@RequestMapping("/api/files") // Base URL for file APIs
public class FileController {

    @Autowired
    private FileService fileService;

    // =========================================================
    // Upload File API
    // =========================================================
    @Operation(
        summary = "Upload File",
        description = "Uploads a file to the server"
    )

    @ApiResponses(value = {

        @ApiResponse(
            responseCode = "200",
            description = "File uploaded successfully"
        ),

        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )

    public ResponseEntity<FileEntity> uploadFile(

            // Receives uploaded file
            @RequestParam("file") MultipartFile file

    ) throws IOException {

        // Calls service layer to save file
        return ResponseEntity.ok(
                fileService.uploadFile(file)
        );
    }

    // =========================================================
    // Download File API
    // =========================================================
    @Operation(
        summary = "Download File",
        description = "Downloads file using file ID"
    )

    @ApiResponses(value = {

        @ApiResponse(
            responseCode = "200",
            description = "File downloaded successfully"
        ),

        @ApiResponse(
            responseCode = "404",
            description = "File not found"
        )
    })

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(

            // File ID from URL
            @PathVariable Long id

    ) throws IOException {

        // Fetch file from service layer
        File file = fileService.getFile(id);

        // Creates input stream for downloading
        InputStreamResource resource =
                new InputStreamResource(
                        new FileInputStream(file)
                );

        // Returns downloadable response
        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + file.getName()
                )

                .contentType(MediaType.APPLICATION_OCTET_STREAM)

                .body(resource);
    }
}