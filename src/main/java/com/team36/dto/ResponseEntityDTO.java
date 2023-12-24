package com.team36.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ResponseEntityDTO {

        private String message;
        private String creationTime;
        private String lastModifiedTime;

        private String fileContent;
        private String fileSize;
        private String filePath;


}
