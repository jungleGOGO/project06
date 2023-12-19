package com.team36.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDataRequest {
    private String filename;
    private String cssfilename;
    private String jsfilename;
    private String htmlfilename;
    private String codeContent;
    private String cssContent;
    private String jsContent;
    private String htmlContent;
}
