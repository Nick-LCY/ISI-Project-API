package com.group3.apiserver.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileProcessingUtil {
    private final String STATIC_FILE_DIR_ADDRESS;

    public FileProcessingUtil() throws IOException {
        STATIC_FILE_DIR_ADDRESS = new ClassPathResource("static").getFile().getAbsolutePath() + "\\";
    }

    public String saveFile(MultipartFile file) throws IOException{
        String originFileName = file.getOriginalFilename();
        assert originFileName != null;
        String fileName = System.currentTimeMillis() + originFileName.substring(originFileName.indexOf('.'));
        String addr = STATIC_FILE_DIR_ADDRESS + fileName;
        Files.write(Paths.get(addr), file.getBytes());
        return "http://localhost:9981/static/" + fileName;
    }

    public Boolean deleteFile(String fileName) {
        File file = new File(STATIC_FILE_DIR_ADDRESS + fileName);
        return file.delete();
    }
}
