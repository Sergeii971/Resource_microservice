package com.os.course.model.dto;

import lombok.Data;

@Data
public class Mp3FileInformationDto {
    private Long id;

    private String name;
    private String contentType;
    private Long size;
    private String url;
}
