package com.internship.tool.controller;

import com.internship.tool.entity.FileEntity;
import com.internship.tool.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FileController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("File Controller Tests")
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    private FileEntity testFileEntity;
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() {

        testFileEntity = new FileEntity();
        testFileEntity.setId(1L);
        testFileEntity.setFileName("test-document.pdf");
        testFileEntity.setFileType("application/pdf");
        testFileEntity.setSize(1024L);
        testFileEntity.setFilePath("/uploads/test-document.pdf");

        testFile = new MockMultipartFile(
                "file",
                "test-document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "PDF content".getBytes()
        );
    }

    // ============ UPLOAD ============
    @Test
    void testUploadFileSuccess() throws Exception {

        when(fileService.uploadFile(any()))
                .thenReturn(testFileEntity);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(testFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.fileName", is("test-document.pdf")))
                .andExpect(jsonPath("$.fileType", is("application/pdf")))
                .andExpect(jsonPath("$.size", is(1024)));
    }

    @Test
    void testUploadEmptyFile() throws Exception {

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/api/files/upload")
                        .file(emptyFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadFileError() throws Exception {

        when(fileService.uploadFile(any()))
                .thenThrow(new IOException("Upload failed"));

        mockMvc.perform(multipart("/api/files/upload")
                        .file(testFile))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUploadMissingFile() throws Exception {

        mockMvc.perform(multipart("/api/files/upload"))
                .andExpect(status().isBadRequest());
    }

    // ============ DOWNLOAD ============
    @Test
    void testDownloadFileSuccess() throws Exception {

        File tmp = File.createTempFile("test-document", ".pdf");
        try (FileWriter fw = new FileWriter(tmp)) {
            fw.write("dummy");
        }
        tmp.deleteOnExit();

        when(fileService.getFile(1L)).thenReturn(tmp);

        mockMvc.perform(get("/api/files/1"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Disposition", containsString("attachment")));
    }

    @Test
    void testDownloadFileNotFound() throws Exception {

        when(fileService.getFile(999L))
                .thenThrow(new RuntimeException("File not found"));

        mockMvc.perform(get("/api/files/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDownloadHeaders() throws Exception {

        File tmp = File.createTempFile("test-document", ".pdf");
        try (FileWriter fw = new FileWriter(tmp)) {
            fw.write("dummy");
        }
        tmp.deleteOnExit();

        when(fileService.getFile(1L)).thenReturn(tmp);

        mockMvc.perform(get("/api/files/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        containsString("attachment")));
    }

    // ============ RESPONSE STRUCTURE ============
    @Test
    void testResponseStructure() throws Exception {

        when(fileService.uploadFile(any()))
                .thenReturn(testFileEntity);

        mockMvc.perform(multipart("/api/files/upload")
                        .file(testFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("id")))
                .andExpect(jsonPath("$", hasKey("fileName")))
                .andExpect(jsonPath("$", hasKey("fileType")))
                .andExpect(jsonPath("$", hasKey("size")))
                .andExpect(jsonPath("$", hasKey("filePath")));
    }
}