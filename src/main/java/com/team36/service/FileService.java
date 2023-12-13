
package com.team36.service;


import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Service
public class FileService {

    public void renameFile(String currentFilename, String newFilename) throws IOException {
        Path sourcePath = Paths.get(currentFilename);
        Path targetPath = Paths.get(newFilename);

        // 파일을 이동하여 이름을 변경합니다.
        Files.move(sourcePath, targetPath);
    }
}
