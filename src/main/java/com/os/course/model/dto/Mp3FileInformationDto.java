package com.os.course.model.dto;

import lombok.Data;

@Data
public class Mp3FileInformationDto implements BaseDto {
    private Long id;
    private String name;
    private String contentType;
    private Long size;
    private String url;
}
